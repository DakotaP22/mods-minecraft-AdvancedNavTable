package com.dakotapease.tabletnav.index;

import com.dakotapease.tabletnav.TabletNavMod;
import com.dakotapease.tabletnav.content.TabletNavigationTarget;
import dev.simulated_team.simulated.navigation.NavigationTarget;
import dev.simulated_team.simulated.registries.SimRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TabletNavTargets {

    public static final DeferredRegister<NavigationTarget> REGISTRY =
        DeferredRegister.create(SimRegistries.NAVIGATION_TARGET, TabletNavMod.MOD_ID);

    public static final DeferredHolder<NavigationTarget, TabletNavigationTarget> TABLET =
        REGISTRY.register("tablet", TabletNavigationTarget::new);
}
