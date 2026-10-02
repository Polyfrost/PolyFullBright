package org.polyfrost.fullbright.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.polyfrost.fullbright.FullBright;
//? if >=26 {
/*import net.minecraft.client.renderer.LightmapRenderStateExtractor;
*///?} else {
import net.minecraft.client.renderer.LightTexture;
//?}

//? if >=26 {
/*@Mixin(LightmapRenderStateExtractor.class)
*///?} else {
@Mixin(LightTexture.class)
//?}
public class LightTextureMixin {
    //? if >=26 {
    /*@ModifyExpressionValue(method = "extract", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 0))
    *///?} else {
    @ModifyExpressionValue(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
    //?}
    private float modifyLightLevel(float original) {
        return FullBright.config.enable ? FullBright.config.lightLevel : original;
    }
}
