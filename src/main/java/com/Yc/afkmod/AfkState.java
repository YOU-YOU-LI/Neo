package com.yourname.afkmod;

public class AfkState {
    public static boolean isAfk = false;
    private static int tickCounter = 0;

    public static void tick() {
        if (isAfk) {
            tickCounter++;
        } else {
            tickCounter = 0;
        }
    }

    // 每12000 tick（10分钟）触发一次防掉线
    public static boolean shouldShake() {
        return isAfk && tickCounter >= 12000;
    }

    public static void resetShakeTimer() {
        tickCounter = 0;
    }
}