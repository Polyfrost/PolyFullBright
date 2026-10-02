package org.polyfrost.fullbright.legacy;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

import org.polyfrost.fullbright.FullBright;

public final class LegacyLight {
    public static boolean shouldBrighten() {
        if (!isEnabled()) return false;

        MinecraftServer server = MinecraftServer.getInstance();
        return server == null || !server.isOnSameThread();
    }

    public static boolean shouldBrightenOnClient() {
        return shouldBrighten() && Minecraft.getInstance().isOnSameThread();
    }

    public static int lightLevel() {
        return FullBright.config.lightLevel;
    }

    public static void reloadRenderers() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.worldRenderer != null) {
            mc.worldRenderer.reload();
        }
    }

    private static boolean isEnabled() {
        return FullBright.config != null && FullBright.config.enable;
    }
}
