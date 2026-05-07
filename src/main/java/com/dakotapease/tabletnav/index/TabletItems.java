package com.dakotapease.tabletnav.index;

import com.dakotapease.tabletnav.TabletNavMod;
import dev.simulated_team.simulated.data.SimDataComponents;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class TabletItems {

    public static final DeferredRegister<Item> REGISTRY =
        DeferredRegister.create(Registries.ITEM, TabletNavMod.MOD_ID);

    public static final DeferredHolder<Item, Item> TABLET =
        REGISTRY.register("tablet", () ->
            new Item(new Item.Properties()
                .stacksTo(1)
                // Bake the NavigationTarget type so the navtable slot accepts this item.
                // TabletNavTargets.TABLET is resolved after NavigationTarget registry fires,
                // which precedes the ITEM RegisterEvent in NeoForge's ordering.
                .component(SimDataComponents.TARGET, TabletNavTargets.TABLET.get())));
}
