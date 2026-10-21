package org.polyfrost.fullbright.mixins.legacy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;

import org.polyfrost.fullbright.legacy.LegacyLight;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, priority = 1100)
public class GameRendererMixin {
    @Shadow private Minecraft minecraft;
    @Shadow @Final private int[] lightMapPixels;

    @Inject(method = "updateLightMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/texture/DynamicTexture;upload()V"))
    private void fullbright$brightenLightMap(float tickDelta, CallbackInfo ci) {
        if (LegacyLight.isEnabled() && this.minecraft.world != null) {
            LegacyLight.brighten(this.lightMapPixels, this.minecraft.world, this.minecraft.options.gamma);
        }
    }
}
