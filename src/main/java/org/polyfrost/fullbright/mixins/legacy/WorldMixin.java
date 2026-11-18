package org.polyfrost.fullbright.mixins.legacy;

import net.minecraft.world.World;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.polyfrost.fullbright.FullBright;
import org.polyfrost.fullbright.legacy.LegacyLight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "updateLight(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void fullbright$skipLightUpdate(CallbackInfoReturnable<Boolean> cir) {
        if (LegacyLight.isEnabledOnClient()) {
            cir.setReturnValue(true);
        }
    }

    // stored light goes stale because updates are skipped, so brighten it here for paths that don't use the lightmap (sky darkening, vignette, shadows)
    @ModifyReturnValue(method = "getRawBrightness(Lnet/minecraft/util/math/BlockPos;)I", at = @At("RETURN"))
    private int fullbright$floorBrightness(int original) {
        return LegacyLight.isEnabledOnClient() ? Math.max(original, FullBright.config.lightLevel) : original;
    }
}
