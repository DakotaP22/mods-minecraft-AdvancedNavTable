package com.dakotapease.tabletnav.content.network;

import com.dakotapease.tabletnav.TabletNavMod;
import com.dakotapease.tabletnav.content.TabletCoords;
import com.dakotapease.tabletnav.content.TabletNavigationTarget;
import com.dakotapease.tabletnav.index.TabletDataComponents;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
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
            if (!player.level().isLoaded(packet.navTablePos())) return;

            if (!(player.level().getBlockEntity(packet.navTablePos()) instanceof NavTableBlockEntity be))
                return;

            if (!(be.getNavTableItem() instanceof TabletNavigationTarget)) return;

            // Simple distance sanity check (64 blocks)
            if (player.blockPosition().distSqr(packet.navTablePos()) > 64 * 64) return;

            ItemStack stack = be.getHeldItem();
            stack.set(TabletDataComponents.TABLET_COORDS.get(), new TabletCoords(packet.x(), packet.z()));
            stack.set(TabletDataComponents.TABLET_ENABLED.get(), packet.enabled());
            be.setChanged();
            be.sendData();
        });
    }
}
