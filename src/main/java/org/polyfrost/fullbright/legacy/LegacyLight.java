package org.polyfrost.fullbright.legacy;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

import org.polyfrost.fullbright.FullBright;

public final class LegacyLight {
    public static boolean isEnabled() {
        return FullBright.config != null && FullBright.config.enable;
    }

    public static boolean isEnabledOnClient() {
        if (!isEnabled() || !Minecraft.getInstance().isOnSameThread()) return false;

        MinecraftServer server = MinecraftServer.getInstance();
        return server == null || !server.isOnSameThread();
    }

    public static void brighten(int[] pixels, World world, float gamma) {
        int floor = level(world, gamma);
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int r = Math.max(pixel >> 16 & 0xFF, floor);
            int g = Math.max(pixel >> 8 & 0xFF, floor);
            int b = Math.max(pixel & 0xFF, floor);
            pixels[i] = 0xFF000000 | r << 16 | g << 8 | b;
        }
    }

    // vanilla's red lightmap channel for a torch-lit block at the configured level, used for all three channels so the floor stays grey
    private static int level(World world, float gamma) {
        float value = world.dimension.getBrightnessTable()[FullBright.config.lightLevel] * 1.5F;
        value = Math.clamp(value * 0.96F + 0.03F, 0.0F, 1.0F);
        float inverse = 1.0F - value;
        value = value * (1.0F - gamma) + (1.0F - inverse * inverse * inverse * inverse) * gamma;
        value = Math.clamp(value * 0.96F + 0.03F, 0.0F, 1.0F);
        return (int) (value * 255.0F);
    }
}
