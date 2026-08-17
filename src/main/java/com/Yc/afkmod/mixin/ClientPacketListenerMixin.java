package com.yourname.afkmod.mixin;

import com.yourname.afkmod.AfkState;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    // 拦截：区块数据（最占流量和CPU）
    @Inject(method = "handleLevelChunk", at = @At("HEAD"), cancellable = true)
    private void onHandleLevelChunk(ClientboundLevelChunkPacketData packet, CallbackInfo ci) {
        if (AfkState.isAfk) {
            ci.cancel(); // 丢掉区块数据包
        }
    }

    // 拦截：方块更新（比如红石、水流变化）
    @Inject(method = "handleSectionBlocksUpdate", at = @At("HEAD"), cancellable = true)
    private void onHandleSectionBlocksUpdate(ClientboundSectionBlocksUpdatePacket packet, CallbackInfo ci) {
        if (AfkState.isAfk) {
            ci.cancel();
        }
    }

    // 拦截：实体运动数据（如其他玩家走动）
    @Inject(method = "handleSetEntityMotion", at = @At("HEAD"), cancellable = true)
    private void onHandleSetEntityMotion(ClientboundSetEntityMotionPacket packet, CallbackInfo ci) {
        if (AfkState.isAfk) {
            ci.cancel();
        }
    }

    // 拦截：方块实体数据（如箱子、熔炉内容）
    @Inject(method = "handleBlockEntityData", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo ci) {
        if (AfkState.isAfk) {
            ci.cancel();
        }
    }
    
    // 注意：KeepAlive（保活数据包）我们没有拦截，它会被正常处理，保证不掉线
}