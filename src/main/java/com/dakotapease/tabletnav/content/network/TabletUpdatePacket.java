package com.dakotapease.tabletnav.content.network;

import com.dakotapease.tabletnav.TabletNavMod;
import com.dakotapease.tabletnav.content.TabletCoords;
import com.dakotapease.tabletnav.content.TabletNavigationTarget;
import com.dakotapease.tabletnav.index.TabletDataComponents;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TabletUpdatePacket(BlockPos navTablePos, int x, int z, boolean enabled)
    implements CustomPacketPayload {

    public static final Type<TabletUpdatePacket> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(TabletNavMod.MOD_ID, "tablet_update"));

    public static final StreamCodec<FriendlyByteBuf, TabletUpdatePacket> STREAM_CODEC =
        StreamCodec.composite(
            BlockPos.STREAM_CODEC, TabletUpdatePacket::navTablePos,
            ByteBufCodecs.INT,     TabletUpdatePacket::x,
            ByteBufCodecs.INT,     TabletUpdatePacket::z,
            ByteBufCodecs.BOOL,    TabletUpdatePacket::enabled,
            TabletUpdatePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleOnServer(TabletUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            Level level = player.level();

            NavTableBlockEntity be = findNavTable(level, packet.navTablePos(), player);
            if (be == null) return;
            if (!(be.getNavTableItem() instanceof TabletNavigationTarget)) return;

            ItemStack stack = be.getHeldItem();
            stack.set(TabletDataComponents.TABLET_COORDS.get(), new TabletCoords(packet.x(), packet.z()));
            stack.set(TabletDataComponents.TABLET_ENABLED.get(), packet.enabled());
            be.setChanged();
            be.sendData();
        });
    }

    private static NavTableBlockEntity findNavTable(Level level, BlockPos pos, ServerPlayer player) {
        // Normal world lookup
        if (level.isLoaded(pos)) {
            if (level.getBlockEntity(pos) instanceof NavTableBlockEntity be
                    && player.blockPosition().distSqr(pos) <= 64 * 64) {
                return be;
            }
        }

        // Contraption (Sable sub-level) lookup: navTablePos is in plot-space
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null || !container.inBounds(pos)) return null;

        LevelChunk chunk = container.getChunk(new ChunkPos(pos));
        if (chunk == null) return null;
        if (!(chunk.getBlockEntity(pos) instanceof NavTableBlockEntity be)) return null;

        // Distance check against the block's projected real-world position
        SubLevel subLevel = Sable.HELPER.getContaining(level, pos);
        if (subLevel == null) return null;

        Vec3 worldPos = subLevel.logicalPose().transformPosition(Vec3.atCenterOf(pos));
        if (player.distanceToSqr(worldPos) > 64 * 64) return null;

        return be;
    }
}
