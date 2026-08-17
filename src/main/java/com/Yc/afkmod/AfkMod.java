package com.yourname.afkmod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AfkMod.MODID)
public class AfkMod {
    public static final String MODID = "afkmod";

    public AfkMod(IEventBus modBus, ModContainer container) {
        // 注册按键（属于Mod总线事件）
        modBus.addListener(this::registerKeys);
        
        // 注册客户端事件（属于游戏总线）
        var bus = NeoForge.EVENT_BUS;
        bus.register(new AfkEventHandler());
        bus.register(new AfkKeybind());
    }

    private void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(AfkKeybind.KEY_MAPPING);
    }
}