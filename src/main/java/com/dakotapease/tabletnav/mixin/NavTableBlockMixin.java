package com.dakotapease.tabletnav.mixin;

import com.dakotapease.tabletnav.content.TabletNavigationTarget;
import com.dakotapease.tabletnav.content.screen.TabletScreen;
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
 * Client-only mixin (declared in the "client" array of tabletnav.mixins.json).
 * Intercepts shift-right-click on the NavTable when a Tablet is slotted
 * and opens the tablet configuration screen.
 */
@Mixin(NavTableBlock.class)
public class NavTableBlockMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void tabletnav$onUseItemOn(
        ItemStack itemStack, BlockState blockState, Level level,
        BlockPos blockPos, Player player, InteractionHand hand,
        BlockHitResult hit, CallbackInfoReturnable<ItemInteractionResult> cir
    ) {
        if (!player.isShiftKeyDown()) return;
        if (!(level.getBlockEntity(blockPos) instanceof NavTableBlockEntity be)) return;
        if (!(be.getNavTableItem() instanceof TabletNavigationTarget)) return;

        // This mixin is client-only so level.isClientSide() is always true here,
        // but the guard makes intent explicit.
        if (level.isClientSide()) {
            TabletScreen.open(blockPos, be.getHeldItem());
        }
        cir.setReturnValue(ItemInteractionResult.SUCCESS);
    }
}
