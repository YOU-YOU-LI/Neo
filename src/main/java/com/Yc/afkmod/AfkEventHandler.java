package com.yourname.afkmod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderEntityEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Random;

@EventBusSubscriber(value = Dist.CLIENT)
public class AfkEventHandler {
    private static final Random RANDOM = new Random();

    // -------- 1. 渲染优化：屏蔽所有实体渲染 --------
    @SubscribeEvent
    public static void onRenderEntity(RenderEntityEvent.Pre event) {
        if (AfkState.isAfk) {
            event.setCanceled(true); // 不渲染任何生物、物品、玩家
        }
    }

    // -------- 1. 渲染优化：屏蔽方块/世界渲染，显示黑屏文字 --------
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (!AfkState.isAfk) return;
        // 在渲染世界的最后阶段，用黑色全屏覆盖，遮住所有方块
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            var mc = Minecraft.getInstance();
            var window = mc.getWindow();
            // 清空颜色缓冲和深度缓冲，变成纯黑
            RenderSystem.clearColor(0, 0, 0, 1);
            RenderSystem.clear(256 | 512, false);
        }
    }

    // -------- 1. 渲染优化：在最上层绘制“挂机中”文字 --------
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!AfkState.isAfk) return;
        GuiGraphics graphics = event.getGuiGraphics();
        var mc = Minecraft.getInstance();
        var font = mc.font;
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        String text = "【 挂 机 中 】";
        int x = (width - font.width(text)) / 2;
        int y = height / 2 - 10;
        // 绘制白色描边文字，清晰可见
        graphics.drawString(font, text, x - 1, y - 1, 0x000000, false);
        graphics.drawString(font, text, x + 1, y + 1, 0x000000, false);
        graphics.drawString(font, text, x, y, 0xFFFFFF, false);
    }

    // -------- 3. 防掉线：每隔10分钟隐蔽晃动视角 --------
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        // 更新计时器
        AfkState.tick();

        // 触发晃动
        if (AfkState.shouldShake()) {
            // 随机生成偏转角度：水平±15°，垂直±5°
            float deltaYaw = (RANDOM.nextFloat() - 0.5f) * 30f;
            float deltaPitch = (RANDOM.nextFloat() - 0.5f) * 10f;
            
            float newYaw = player.getYRot() + deltaYaw;
            float newPitch = player.getXRot() + deltaPitch;
            // 限制俯仰角在-90~90之间
            newPitch = Math.clamp(newPitch, -90f, 90f);

            // 发送移动数据包给服务器（伪装成手动转头）
            boolean onGround = player.onGround();
            player.connection.send(new ServerboundMovePlayerPacket.Rot(newYaw, newPitch, onGround));
            
            // 重置计时器，开始下一个10分钟
            AfkState.resetShakeTimer();
        }
    }
}