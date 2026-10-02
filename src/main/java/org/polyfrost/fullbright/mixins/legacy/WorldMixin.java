package org.polyfrost.fullbright.mixins.legacy;

import net.minecraft.world.World;

import org.polyfrost.fullbright.legacy.LegacyLight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class WorldMixin {
    @Inject(method = "updateLight(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void fullbright$skipLightUpdate(CallbackInfoReturnable<Boolean> cir) {
        if (LegacyLight.shouldBrightenOnClient()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {
            "getBrightness(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)I",
            "getRawBrightness(Lnet/minecraft/util/math/BlockPos;)I",
            "getRawBrightness(Lnet/minecraft/util/math/BlockPos;Z)I",
            "getActualLight(Lnet/minecraft/util/math/BlockPos;)I",
            "findLight(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/LightType;)I"
    }, at = @At("HEAD"), cancellable = true)
    private void fullbright$light(CallbackInfoReturnable<Integer> cir) {
        if (LegacyLight.shouldBrightenOnClient()) {
            cir.setReturnValue(LegacyLight.lightLevel());
        }
    }
}
