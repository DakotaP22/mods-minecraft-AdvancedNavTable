package com.dakotapease.tabletnav.mixin;

import com.dakotapease.tabletnav.content.TabletNavigationTarget;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlock;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Server-side counterpart to NavTableBlockMixin. Cancels the vanilla
 * useItemOn logic (which would eject the Tablet) when the client has
 * opened the tablet configuration screen via a shift-right-click.
 * The client mixin handles screen opening; this one simply prevents the
 * server from dropping the item.
 */
@Mixin(NavTableBlock.class)
public class NavTableBlockServerMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true, remap = false)
    private void tabletnav$onUseItemOn(
        ItemStack itemStack, BlockState blockState, Level level,
        BlockPos blockPos, Player player, InteractionHand hand,
        BlockHitResult hit, CallbackInfoReturnable<ItemInteractionResult> cir
    ) {
        if (!player.isShiftKeyDown()) return;
        if (!(level.getBlockEntity(blockPos) instanceof NavTableBlockEntity be)) return;
        if (!(be.getNavTableItem() instanceof TabletNavigationTarget)) return;

        cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }
}
