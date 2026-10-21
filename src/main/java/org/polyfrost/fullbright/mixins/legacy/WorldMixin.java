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
        if (LegacyLight.isEnabledOnClient()) {
            cir.setReturnValue(true);
        }
    }
}
