package com.yourname.afkmod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT)
public class AfkKeybind {
    public static final KeyMapping KEY_MAPPING = new KeyMapping(
        "key.afkmod.toggle", // 在en_us.json里定义显示名称
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_P,
        "key.categories.afkmod"
    );

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        
        // 每次按下P键切换状态
        while (KEY_MAPPING.consumeClick()) {
            AfkState.isAfk = !AfkState.isAfk;
            // 切换时重置计时器，避免一开就晃动
            AfkState.resetShakeTimer();
            if (AfkState.isAfk) {
                // 进入挂机：关闭游戏声音（可选）
                mc.options.setSoundOn(false);
            } else {
                mc.options.setSoundOn(true);
            }
        }
    }
}