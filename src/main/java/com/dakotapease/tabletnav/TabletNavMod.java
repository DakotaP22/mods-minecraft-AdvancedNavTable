package com.dakotapease.tabletnav;

import com.dakotapease.tabletnav.content.network.TabletUpdatePacket;
import com.dakotapease.tabletnav.index.TabletDataComponents;
import com.dakotapease.tabletnav.index.TabletItems;
import com.dakotapease.tabletnav.index.TabletNavTargets;
import dev.simulated_team.simulated.index.SimDataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(TabletNavMod.MOD_ID)
public class TabletNavMod {

    public static final String MOD_ID = "tabletnav";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    static {
        CREATIVE_TABS.register("main", () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.tabletnav.main"))
                .icon(() -> TabletItems.TABLET.get().getDefaultInstance())
                .displayItems((params, output) -> output.accept(TabletItems.TABLET.get()))
                .build());
    }

    public TabletNavMod(IEventBus modBus, ModContainer modContainer) {
        LOGGER.info("Advanced Nav Table initializing...");

        TabletDataComponents.REGISTRY.register(modBus);
        TabletNavTargets.REGISTRY.register(modBus);
        TabletItems.REGISTRY.register(modBus);
        CREATIVE_TABS.register(modBus);

        modBus.addListener(TabletNavMod::registerPayloads);
        modBus.addListener(TabletNavMod::modifyDefaultComponents);
    }

    private static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        event.modify(TabletItems.TABLET.get(), builder ->
            builder.set(SimDataComponents.TARGET, TabletNavTargets.TABLET.get()));
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
            TabletUpdatePacket.TYPE,
            TabletUpdatePacket.STREAM_CODEC,
            TabletUpdatePacket::handleOnServer);
    }
}
