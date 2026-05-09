package com.dakotapease.tabletnav.index;

import com.dakotapease.tabletnav.TabletNavMod;
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
                .stacksTo(1)));
}
