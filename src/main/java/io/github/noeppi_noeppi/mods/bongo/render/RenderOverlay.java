package io.github.noeppi_noeppi.mods.bongo.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.libx.render.RenderHelper;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.Keybinds;
import io.github.noeppi_noeppi.mods.bongo.config.ClientConfig;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.data.WinCondition;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class RenderOverlay {

    public static final ResourceLocation BINGO_TEXTURE = new ResourceLocation(BongoMod.getInstance().modid, "textures/overlay/bingo.png");
    public static final ResourceLocation BINGO_SLOTS_TEXTURE = new ResourceLocation(BongoMod.getInstance().modid, "textures/overlay/bingo_slots.png");
    public static final ResourceLocation BEACON_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/beacon.png");
    public static final ResourceLocation COMPLETED_TEXTURE = new ResourceLocation(BongoMod.getInstance().modid, "textures/overlay/completed_rects.png");
    public static final ResourceLocation ICONS_TEXTURE = new ResourceLocation(BongoMod.getInstance().modid, "textures/overlay/icons.png");

    @SubscribeEvent
    public void renderChat(RenderGameOverlayEvent.Pre event) {
        // When some elements the bingo card occasionally there are render problems. So we
        // just hide man GUI parts when the bingo card is enlarged.
        if (Keybinds.BIG_OVERLAY.isDown()) {
            if (Minecraft.getInstance().level != null) {
                Bongo bongo = Bongo.get(Minecraft.getInstance().level);
                if (bongo.active()) {
                    RenderGameOverlayEvent.ElementType type = event.getType();
                    if (type == RenderGameOverlayEvent.ElementType.CHAT
                            || type == RenderGameOverlayEvent.ElementType.DEBUG
                            || type == RenderGameOverlayEvent.ElementType.TEXT
                            || type == RenderGameOverlayEvent.ElementType.LAYER
                            || type == RenderGameOverlayEvent.ElementType.BOSSINFO) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent.Post event) {
        PoseStack poseStack = event.getMatrixStack();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.player != null && mc.screen == null && (!mc.options.renderDebug || Keybinds.BIG_OVERLAY.isDown()) && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            Bongo bongo = Bongo.get(mc.level);
            Team team = bongo.getTeam(mc.player);
            if (bongo.active() && (!bongo.running() || team != null)) {
                double padding = 5;
                double px = mc.getWindow().getGuiScaledHeight() / 3.5d;
                double x = padding;
                double y = padding;
                boolean itemNames = false;
                if (Keybinds.BIG_OVERLAY.isDown()) {
                    px = Math.min(mc.getWindow().getGuiScaledWidth() - (2 * padding), mc.getWindow().getGuiScaledHeight() - (6 * padding));
                    x = (mc.getWindow().getGuiScaledWidth() - px) / 2;
                    y = ((mc.getWindow().getGuiScaledHeight() - px) / 2) - (2 * padding);
                    itemNames = true;
                } else {
                    float scale = (float) (double) ClientConfig.bongoMapScaleFactor.get();
                    poseStack.scale(scale, scale, 1);
                }

                poseStack.pushPose();
                poseStack.translate(x, y, 0);
                poseStack.scale((float) px / 138, (float) px / 138, 1);

                RenderSystem.setShaderTexture(0, BINGO_TEXTURE);
                GuiComponent.blit(poseStack, 0, 0, 0, 0, 138, 138, 256, 256);

                poseStack.translate(2, 2, 10);

                for (int ySlot = 0; ySlot < 5; ySlot++) {
                    for (int xSlot = 0; xSlot < 5; xSlot++) {
                        int slot = xSlot + (5 * ySlot);
                        List<Integer> colorCodes = new ArrayList<>();
                        for (DyeColor dc : DyeColor.values()) {
                            if (bongo.getTeam(dc).completed(slot))
                                colorCodes.add(dc.getTextColor());
                        }

                        poseStack.pushPose();
                        poseStack.translate(xSlot * 27, ySlot * 27, 0);

                        renderCompleted(poseStack, buffer, colorCodes);

                        poseStack.popPose();
                    }
                }

                poseStack.translate(-2, -2, 10);

                RenderSystem.setShaderTexture(0, BINGO_SLOTS_TEXTURE);
                for (int ySlot = 0; ySlot < 5; ySlot++) {
                    for (int xSlot = 0; xSlot < 5; xSlot++) {
                        int slot = xSlot + (5 * ySlot);

                        poseStack.pushPose();
                        poseStack.translate(6 + (27 * xSlot), 6 + (27 * ySlot), 0);

                        bongo.task(slot).renderSlot(mc, poseStack, buffer);

                        poseStack.popPose();
                    }
                }

                poseStack.translate(7, 7, 0);

                for (int ySlot = 0; ySlot < 5; ySlot++) {
                    for (int xSlot = 0; xSlot < 5; xSlot++) {
                        int slot = xSlot + (5 * ySlot);
                        Task task = bongo.task(slot);

                        poseStack.pushPose();
                        poseStack.translate(xSlot * 27, ySlot * 27, 0);

                        task.renderSlotContent(mc, poseStack, buffer, itemNames);

                        poseStack.popPose();

                        if (itemNames) {
                            poseStack.pushPose();
                            poseStack.translate((xSlot * 27) + 8, (ySlot * 27) + 4, 700);
                            poseStack.scale(0.3f, 0.3f, 1);

                            RenderHelper.renderText(task.getTranslatedName(), poseStack);

                            poseStack.translate(0, 8 / 0.3, 10);
                            poseStack.scale(0.8f, 0.8f, 1);

                            RenderHelper.renderText(task.getTranslatedContentName(), poseStack);

                            poseStack.popPose();
                        }

                        if (team != null) {
                            if (team.completed(slot)) {
                                poseStack.pushPose();
                                poseStack.translate(0, 0, 800);
                                RenderSystem.setShaderTexture(0, BEACON_TEXTURE);
                                GuiComponent.blit(poseStack, xSlot * 27, ySlot * 27, 90, 222, 16, 16, 256, 256);
                                poseStack.popPose();
                            } else if (team.locked(slot)) {
                                poseStack.pushPose();
                                poseStack.translate(xSlot * 27, ySlot * 27, 800);
                                RenderSystem.setShaderTexture(0, BEACON_TEXTURE);
                                poseStack.scale(16f/15f, 16f/15f, 16f/15f);
                                GuiComponent.blit(poseStack, 0, 0, 113, 222, 15, 15, 256, 256);
                                poseStack.popPose();
                            }
                        }
                    }
                }

                if (!itemNames) {
                    List<String> lines = new ArrayList<>();

                    if (bongo.getSettings().winCondition != WinCondition.DEFAULT) {
                        lines.add(I18n.get("bongo.wc." + bongo.getSettings().winCondition.id));
                    }

                    if (bongo.getSettings().teleportsPerTeam != 0 && !bongo.won()) {
                        boolean hasRunningTeam = team != null && bongo.running();
                        int tpLeft = hasRunningTeam ? team.teleportsLeft() : bongo.getSettings().teleportsPerTeam;
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
                            } else if (bongo.hasTimeLimit()) {
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
                    } else if (bongo.active() && bongo.hasTimeLimit() && bongo.getSettings().maxTime >= 0) {
                        int sec = bongo.getSettings().maxTime % 60;
                        int min = (bongo.getSettings().maxTime / 60) % 60;
                        int hour = bongo.getSettings().maxTime / 3600;
                        lines.add(I18n.get("bongo.maxtime") + Util.formatTime(hour, min, sec));
                    }

                    if (!lines.isEmpty()) {
                        poseStack.translate(0, 133, 800);
                        poseStack.scale(1.3f, 1.3f, 1);

                        for (int i = 0; i < lines.size(); i++) {
                            mc.font.draw(poseStack, lines.get(i), 0, (mc.font.lineHeight + 1) * i, 0xFFFFFF);
                        }
                    }
                }

                poseStack.popPose();
                buffer.endBatch();
            }
        }
    }

    public void renderCompleted(PoseStack poseStack, MultiBufferSource buffer, List<Integer> colorCodes) {
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

        poseStack.pushPose();
        poseStack.scale(26 / 24f, 26 / 24f, 0);
        RenderSystem.setShaderTexture(0, COMPLETED_TEXTURE);

        for (int rect = 0; rect < rects.length; rect++) {
            if (rect >= colorCodes.size())
                break;

            RenderHelper.rgb(colorCodes.get(rect));
            GuiComponent.blit(poseStack, rects[rect][0], rects[rect][1], 0, 0, rects[rect][2] - rects[rect][0], rects[rect][3] - rects[rect][1], 256, 256);
            RenderHelper.resetColor();
        }
        poseStack.popPose();
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
