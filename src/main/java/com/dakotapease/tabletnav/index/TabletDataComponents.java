package com.dakotapease.tabletnav.index;

import com.dakotapease.tabletnav.TabletNavMod;
import com.dakotapease.tabletnav.content.TabletCoords;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TabletDataComponents {

    public static final DeferredRegister<DataComponentType<?>> REGISTRY =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TabletNavMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TabletCoords>> TABLET_COORDS =
        REGISTRY.register("tablet_coords", () ->
            DataComponentType.<TabletCoords>builder()
                .persistent(TabletCoords.CODEC)
                .networkSynchronized(TabletCoords.STREAM_CODEC)
                .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> TABLET_ENABLED =
        REGISTRY.register("tablet_enabled", () ->
            DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
                .build());
}
