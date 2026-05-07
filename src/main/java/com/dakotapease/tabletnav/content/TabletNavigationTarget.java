package com.dakotapease.tabletnav.content;

import com.dakotapease.tabletnav.index.TabletDataComponents;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.navigation.NavigationTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TabletNavigationTarget implements NavigationTarget {

    @Override
    public @Nullable Vec3 getTarget(NavTableBlockEntity navBE, ItemStack self) {
        if (!self.getOrDefault(TabletDataComponents.TABLET_ENABLED.get(), false)) return null;
        TabletCoords coords = self.get(TabletDataComponents.TABLET_COORDS.get());
        if (coords == null) return null;
        return new Vec3(coords.x() + 0.5, navBE.getProjectedSelfPos().y, coords.z() + 0.5);
    }

    @Override
    public int getMaxRange() {
        return 0; // uncapped
    }

    @Override
    public double distanceToTarget(NavTableBlockEntity navBE) {
        ItemStack stack = navBE.getHeldItem();
        if (!stack.getOrDefault(TabletDataComponents.TABLET_ENABLED.get(), false)) return 0;
        TabletCoords coords = stack.get(TabletDataComponents.TABLET_COORDS.get());
        if (coords == null) return 0;
        Vec3 selfPos = navBE.getProjectedSelfPos();
        Vec3 target = new Vec3(coords.x() + 0.5, selfPos.y, coords.z() + 0.5);
        return selfPos.distanceTo(target);
    }
}
