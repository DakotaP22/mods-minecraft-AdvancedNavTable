package com.example.create_colonies;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import com.example.create_colonies.placement.BeltPlacementHandler;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(SampleMod.MODID)
public class SampleMod {

    public static final String MODNAME = "Sample Mod";
    public static final String MODID = "sample_mod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SampleMod(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info(MODNAME + " initializing...");
    }

    
}
