package io.github.noeppi_noeppi.mods.bongo.render;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.Keybinds;
import io.github.noeppi_noeppi.mods.bongo.config.ClientConfig;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.data.WinCondition;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.moddingx.libx.render.RenderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RenderOverlay implements IGuiOverlay {
    
    public static RenderOverlay INSTANCE = new RenderOverlay();
    
    private RenderOverlay() {
        
    }

    public static final ResourceLocation BINGO_TEXTURE = new ResourceLocation(BongoMod.getInstance().modid, "textures/overlay/bingo.png");
    public static final ResourceLocation BINGO_SLOTS_TEXTURE = new ResourceLocation(BongoMod.getInstance().modid, "textures/overlay/bingo_slots.png");
    public static final ResourceLocation BEACON_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/beacon.png");
    public static final ResourceLocation BARRIER_TEXTURE = new ResourceLocation("minecraft", "textures/item/barrier.png");
    public static final ResourceLocation COMPLETED_TEXTURE = new ResourceLocation(BongoMod.getInstance().modid, "textures/overlay/completed_rects.png");
    public static final ResourceLocation ICONS_TEXTURE = new ResourceLocation(BongoMod.getInstance().modid, "textures/overlay/icons.png");

    public static final Set<ResourceLocation> HIDDEN_OVERLAYS = Set.of(
            VanillaGuiOverlay.CHAT_PANEL.id(),
            VanillaGuiOverlay.DEBUG_TEXT.id(),
            VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(),
            VanillaGuiOverlay.PLAYER_LIST.id()
    );
    
    @SubscribeEvent
    public static void renderGuiPart(RenderGuiOverlayEvent.Pre event) {
        if (HIDDEN_OVERLAYS.contains(event.getOverlay().id())) {
            if (Keybinds.BIG_OVERLAY.isDown() && Minecraft.getInstance().level != null) {
                Bongo bongo = Bongo.get(Minecraft.getInstance().level);
                if (bongo.active()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null && mc.screen == null && (!mc.options.renderDebug || Keybinds.BIG_OVERLAY.isDown())) {
            Bongo bongo = Bongo.get(mc.level);
            Team team = bongo.getTeam(mc.player);
            if (bongo.active() && (!bongo.running() || team != null)) {
                gui.setupOverlayRenderState(false, false);
                graphics.pose().pushPose();
                
                double padding = 5;
                double px = height / 3.5d;
                double x = padding;
                double y = padding;
                boolean itemNames = false;
                if (Keybinds.BIG_OVERLAY.isDown()) {
                    px = Math.min(width - (2 * padding), height - (6 * padding));
                    x = (width - px) / 2;
                    y = ((height - px) / 2) - (2 * padding);
                    itemNames = true;
                } else {
                    px *= (float) (double) ClientConfig.bongoMapScaleFactor.get();
                    if (ClientConfig.bongoMapOnTheRight.get()) {
                        x = width - padding - px;
                    }
                }

                graphics.pose().translate(x, y, 0);
                graphics.pose().scale((float) (px / 138), (float) (px / 138), 1);

                graphics.blit(BINGO_TEXTURE, 0, 0, 0, 0, 138, 138, 256, 256);

                graphics.pose().translate(2, 2, 10);

                for (int ySlot = 0; ySlot < 5; ySlot++) {
                    for (int xSlot = 0; xSlot < 5; xSlot++) {
                        int slot = xSlot + (5 * ySlot);
                        List<Integer> colorCodes = new ArrayList<>();
                        for (DyeColor dc : DyeColor.values()) {
                            if (!bongo.getTeam(dc).isEmpty() && bongo.getTeam(dc).completion().has(slot))
                                colorCodes.add(dc.getTextColor());
                        }

                        graphics.pose().pushPose();
                        graphics.pose().translate(xSlot * 27, ySlot * 27, 0);

                        renderCompleted(graphics, colorCodes);

                        graphics.pose().popPose();
                    }
                }

                graphics.pose().translate(-2, -2, 10);

                for (int ySlot = 0; ySlot < 5; ySlot++) {
                    for (int xSlot = 0; xSlot < 5; xSlot++) {
                        int slot = xSlot + (5 * ySlot);

                        graphics.pose().pushPose();
                        graphics.pose().translate(6 + (27 * xSlot), 6 + (27 * ySlot), 0);

                        bongo.task(slot).renderSlot(mc, graphics);

                        graphics.pose().popPose();
                    }
                }

                graphics.pose().translate(7, 7, 0);

                for (int ySlot = 0; ySlot < 5; ySlot++) {
                    for (int xSlot = 0; xSlot < 5; xSlot++) {
                        int slot = xSlot + (5 * ySlot);
                        Task task = bongo.task(slot);

                        graphics.pose().pushPose();
                        graphics.pose().translate(xSlot * 27, ySlot * 27, 0);

                        task.renderSlotContent(mc, graphics, itemNames);

                        graphics.pose().popPose();

                        boolean hasOverlayIcon = team != null && (team.completed(slot) || team.locked(slot));
                        if (!hasOverlayIcon && task.inverted() && task.customTexture() == null) {
                            graphics.pose().pushPose();
                            graphics.pose().translate(xSlot * 27 - 4, ySlot * 27 - 4, 650);
                            graphics.pose().scale(24f/16f, 24f/16f, 24f/16f);
                            graphics.blit(BARRIER_TEXTURE, 0, 0, 0, 0, 16, 16, 16, 16);
                            graphics.pose().popPose();
                        }
                        
                        if (itemNames) {
                            graphics.pose().pushPose();
                            graphics.pose().translate((xSlot * 27) + 8, (ySlot * 27) + 4, 700);
                            graphics.pose().scale(0.3f, 0.3f, 1);

                            renderTextWithBackground(graphics, task.typeName());

                            graphics.pose().translate(0, 8 / 0.3, 10);
                            graphics.pose().scale(0.8f, 0.8f, 1);

                            renderTextWithBackground(graphics, task.renderDisplayName(mc));

                            graphics.pose().popPose();
                        }

                        if (team != null) {
                            if (team.completed(slot)) {
                                graphics.pose().pushPose();
                                graphics.pose().translate(0, 0, 800);
                                graphics.blit(BEACON_TEXTURE, xSlot * 27, ySlot * 27, 90, 222, 16, 16, 256, 256);
                                graphics.pose().popPose();
                            } else if (team.locked(slot)) {
                                graphics.pose().pushPose();
                                graphics.pose().translate(xSlot * 27, ySlot * 27, 800);
                                graphics.pose().scale(16f/15f, 16f/15f, 16f/15f);
                                graphics.blit(BEACON_TEXTURE, 0, 0, 113, 222, 15, 15, 256, 256);
                                graphics.pose().popPose();
                            }
                        }
                    }
                }

                if (!itemNames) {
                    List<String> lines = new ArrayList<>();

                    if (bongo.getSettings().game().winCondition() != WinCondition.DEFAULT) {
                        lines.add(I18n.get("bongo.wc." + bongo.getSettings().game().winCondition().id));
                    }

                    if (bongo.getSettings().game().teleportsPerTeam() != 0 && !bongo.won()) {
                        boolean hasRunningTeam = team != null && bongo.running();
                        int tpLeft = hasRunningTeam ? team.teleportsLeft() : bongo.getSettings().game().teleportsPerTeam();
                        String tpLeftStr = tpLeft < 0 ? I18n.get("bongo.infinite") : Integer.toString(tpLeft);
                        lines.add(I18n.get(hasRunningTeam ? "bongo.tp_left": "bongo.tp_team", tpLeftStr));
                    }

                    if (bongo.running() || bongo.won()) {
                        if (!bongo.won() && bongo.tasksForWin() > 0) {
                            lines.add(I18n.get("bongo.instantwin") + bongo.tasksForWin());
                        } else {
                            long millis;
                            boolean isTimer = true;
                            if (bongo.won()) {
                                millis = bongo.ranUntil() - bongo.runningSince();
                            } else if (bongo.getSettings().game().time().limited()) {
                                isTimer = false;
                                millis = Math.max(0, bongo.runningUntil() - System.currentTimeMillis());
                            } else {
                                millis = System.currentTimeMillis() - bongo.runningSince();
                            }
                            int decimal = (int) ((millis / 100) % 10);
                            int sec = (int) ((millis / 1000) % 60);
                            int min = (int) ((millis / 60000) % 60);
                            int hour = (int) millis / 3600000;
                            lines.add(I18n.get(isTimer ? "bongo.timer" : "bongo.timeleft") + Util.formatTime(hour, min, sec, decimal));
                        }
                    } else if (bongo.active() && bongo.getSettings().game().time().limit().orElse(-1) >= 0) {
                        int maxTime = bongo.getSettings().game().time().limit().orElse(0);
                        int sec = maxTime % 60;
                        int min = (maxTime / 60) % 60;
                        int hour = maxTime / 3600;
                        lines.add(I18n.get("bongo.maxtime") + Util.formatTime(hour, min, sec));
                    }

                    if (!lines.isEmpty()) {
                        if (ClientConfig.bongoMapOnTheRight.get()) {
                            graphics.pose().translate(124, 133, 800);
                        } else {
                            graphics.pose().translate(0, 133, 800);
                        }
                        graphics.pose().scale(1.3f, 1.3f, 1);
                        
                        RenderHelper.resetColor();
                        for (int i = 0; i < lines.size(); i++) {
                            int off = ClientConfig.bongoMapOnTheRight.get() ? -mc.font.width(lines.get(i)) : 0;
                            graphics.drawString(mc.font, lines.get(i), off, (mc.font.lineHeight + 1) * i, 0xFFFFFF, false);
                        }
                    }
                }

                graphics.pose().popPose();
            }
        }
    }

    public void renderCompleted(GuiGraphics graphics, List<Integer> colorCodes) {
        if (colorCodes.isEmpty())
            return;
        int[][] rects;
        if (colorCodes.size() <= 1) {
            rects = RECTS_1;
        } else if (colorCodes.size() <= 2) {
            rects = RECTS_2;
        } else if (colorCodes.size() <= 4) {
            rects = RECTS_4;
        } else if (colorCodes.size() <= 6) {
            rects = RECTS_6;
        } else if (colorCodes.size() <= 8) {
            rects = RECTS_8;
        } else {
            rects = RECTS_16;
        }

        graphics.pose().pushPose();
        graphics.pose().scale(26 / 24f, 26 / 24f, 0);

        for (int rect = 0; rect < rects.length; rect++) {
            if (rect >= colorCodes.size())
                break;

            RenderHelper.rgb(colorCodes.get(rect));
            graphics.blit(COMPLETED_TEXTURE, rects[rect][0], rects[rect][1], 0, 0, rects[rect][2] - rects[rect][0], rects[rect][3] - rects[rect][1], 256, 256);
            RenderHelper.resetColor();
        }
        graphics.pose().popPose();
    }

    public static void renderTextWithBackground(GuiGraphics graphics, Component text) {
        renderTextWithBackground(graphics, text.getVisualOrderText());
    }
    
    public static void renderTextWithBackground(GuiGraphics graphics, FormattedCharSequence text) {
        if (Minecraft.getInstance().font.width(text) == 0) return;
        float widthHalf = Minecraft.getInstance().font.width(text) / 2f;
        float heightHalf = Minecraft.getInstance().font.lineHeight / 2f;

        graphics.pose().pushPose();
        graphics.pose().translate(-(widthHalf + 2), -(heightHalf + 2), 0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(0.2f, 0.2f, 0.2f, 0.8f);
        graphics.blit(RenderHelper.TEXTURE_WHITE, 0, 0, 0, 0, (int) (2 * widthHalf) + 4, (int) (2 * heightHalf) + 4, 256, 256);
        RenderSystem.disableBlend();
        graphics.pose().translate(widthHalf + 2, heightHalf + 2, 10);

        RenderHelper.resetColor();
        graphics.drawString(Minecraft.getInstance().font, text, -widthHalf, -heightHalf, 0xFFFFFF, false);
        graphics.pose().popPose();
    }
    
    private static final int[][] RECTS_1 = new int[][]{
            { 0, 0, 24, 24 }
    };

    private static final int[][] RECTS_2 = new int[][]{
            { 0, 0, 12, 24 },
            { 12, 0, 24, 24 }
    };

    private static final int[][] RECTS_4 = new int[][]{
            { 0, 0, 12, 12 },
            { 12, 0, 24, 12 },
            { 0, 12, 12, 24 },
            { 12, 12, 24, 24 }
    };

    private static final int[][] RECTS_6 = new int[][]{
            { 0, 0, 12, 8 },
            { 12, 0, 24, 8 },
            { 0, 8, 12, 16 },
            { 12, 8, 24, 16 },
            { 0, 16, 12, 24 },
            { 12, 16, 24, 24 }
    };

    private static final int[][] RECTS_8 = new int[][]{
            { 0, 0, 8, 8 },
            { 8, 0, 16, 8 },
            { 16, 0, 24, 8 },
            { 0, 8, 8, 16 },
            { 16, 8, 24, 16 },
            { 0, 16, 8, 24 },
            { 8, 16, 16, 24 },
            { 16, 16, 24, 24 }
    };

    private static final int[][] RECTS_16 = new int[][]{
            { 0, 0, 5, 4 },
            { 5, 0, 10, 4 },
            { 10, 0, 15, 4 },
            { 15, 0, 20, 4 },
            { 20, 0, 24, 5 },
            { 0, 4, 4, 9 },
            { 20, 5, 24, 10 },
            { 0, 9, 4, 14 },
            { 20, 10, 24, 15 },
            { 0, 14, 4, 19 },
            { 20, 15, 24, 10 },
            { 0, 19, 4, 24 },
            { 4, 20, 9, 24 },
            { 9, 20, 14, 24 },
            { 14, 20, 19, 24 },
            { 19, 20, 24, 24 },
    };
}
