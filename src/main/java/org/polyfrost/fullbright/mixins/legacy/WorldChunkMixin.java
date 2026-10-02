package org.polyfrost.fullbright.mixins.legacy;

import net.minecraft.world.chunk.WorldChunk;

import org.polyfrost.fullbright.legacy.LegacyLight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {
    @Inject(method = {
            "getLight(Lnet/minecraft/world/LightType;Lnet/minecraft/util/math/BlockPos;)I",
            "getLight(Lnet/minecraft/util/math/BlockPos;I)I"
    }, at = @At("HEAD"), cancellable = true)
    private void fullbright$light(CallbackInfoReturnable<Integer> cir) {
        if (LegacyLight.shouldBrighten()) {
            cir.setReturnValue(LegacyLight.lightLevel());
        }
    }
}
