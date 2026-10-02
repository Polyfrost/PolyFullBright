package org.polyfrost.fullbright.mixins.legacy;

import org.polyfrost.fullbright.legacy.LegacyLight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "dev.rdh.argentum.impl.world.cloned.ChunkRenderContext", remap = false)
public class ArgentumChunkRenderContextMixin {
    @Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    private void fullbright$light(CallbackInfoReturnable<Integer> cir) {
        if (LegacyLight.shouldBrighten()) {
            cir.setReturnValue(LegacyLight.lightLevel());
        }
    }
}
