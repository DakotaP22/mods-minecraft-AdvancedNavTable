package com.dakotapease.tabletnav.content.screen;

import com.dakotapease.tabletnav.content.TabletCoords;
import com.dakotapease.tabletnav.content.network.TabletUpdatePacket;
import com.dakotapease.tabletnav.index.TabletDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class TabletScreen extends Screen {

    private static final int PANEL_W = 200;
    private static final int PANEL_H = 120;

    private final BlockPos navTablePos;
    private int coordX;
    private int coordZ;
    private boolean enabled;

    private EditBox xBox;
    private EditBox zBox;
    private Button toggleButton;

    private TabletScreen(BlockPos navTablePos, int coordX, int coordZ, boolean enabled) {
        super(Component.translatable("screen.tabletnav.tablet"));
        this.navTablePos = navTablePos;
        this.coordX = coordX;
        this.coordZ = coordZ;
        this.enabled = enabled;
    }

    public static void open(BlockPos navTablePos, ItemStack stack) {
        TabletCoords coords = stack.get(TabletDataComponents.TABLET_COORDS.get());
        int x = coords != null ? coords.x() : 0;
        int z = coords != null ? coords.z() : 0;
        boolean en = stack.getOrDefault(TabletDataComponents.TABLET_ENABLED.get(), false);
        Minecraft.getInstance().setScreen(new TabletScreen(navTablePos, x, z, en));
    }

    @Override
    protected void init() {
        int left = (width - PANEL_W) / 2;
        int top  = (height - PANEL_H) / 2;

        // X coordinate
        xBox = new EditBox(font, left + 10, top + 30, 80, 20,
            Component.translatable("field.tabletnav.x"));
        xBox.setMaxLength(10);
        xBox.setValue(String.valueOf(coordX));
        xBox.setFilter(s -> s.isEmpty() || s.equals("-") || isParsableInt(s));
        addRenderableWidget(xBox);

        // Z coordinate
        zBox = new EditBox(font, left + 110, top + 30, 80, 20,
            Component.translatable("field.tabletnav.z"));
        zBox.setMaxLength(10);
        zBox.setValue(String.valueOf(coordZ));
        zBox.setFilter(s -> s.isEmpty() || s.equals("-") || isParsableInt(s));
        addRenderableWidget(zBox);

        // Enable / disable toggle
        toggleButton = Button.builder(toggleLabel(), btn -> {
            enabled = !enabled;
            btn.setMessage(toggleLabel());
        }).bounds(left + 50, top + 70, 100, 20).build();
        addRenderableWidget(toggleButton);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float delta) {
        renderBackground(gfx, mouseX, mouseY, delta);

        int left = (width - PANEL_W) / 2;
        int top  = (height - PANEL_H) / 2;

        gfx.fill(left, top, left + PANEL_W, top + PANEL_H, 0xCC000000);
        gfx.drawCenteredString(font,
            Component.translatable("screen.tabletnav.tablet"),
            width / 2, top + 10, 0xFFFFFF);
        gfx.drawString(font, "X:", left + 10, top + 18, 0xAAAAAA);
        gfx.drawString(font, "Z:", left + 110, top + 18, 0xAAAAAA);

        super.render(gfx, mouseX, mouseY, delta);
    }

    @Override
    public void removed() {
        // Parse fields — fall back to previous value on blank/invalid input
        coordX = parseOrDefault(xBox.getValue(), coordX);
        coordZ = parseOrDefault(zBox.getValue(), coordZ);
        PacketDistributor.sendToServer(
            new TabletUpdatePacket(navTablePos, coordX, coordZ, enabled));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // -----------------------------------------------------------------------

    private Component toggleLabel() {
        return enabled
            ? Component.translatable("button.tabletnav.enabled")
            : Component.translatable("button.tabletnav.disabled");
    }

    private static boolean isParsableInt(String s) {
        try { Integer.parseInt(s); return true; } catch (NumberFormatException e) { return false; }
    }

    private static int parseOrDefault(String s, int fallback) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return fallback; }
    }
}
