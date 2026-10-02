package org.polyfrost.fullbright.config;

import org.polyfrost.fullbright.FullBright;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;

public class FullBrightConfig extends Config {
    public FullBrightConfig() {
        super(FullBright.ID + ".json", FullBright.NAME, Category.QOL);

        loadFrom("patcher.toml");

        //? if <1.9 {
        addCallback("enable", (Runnable) org.polyfrost.fullbright.legacy.LegacyLight::reloadRenderers);
        addCallback("lightLevel", (Runnable) org.polyfrost.fullbright.legacy.LegacyLight::reloadRenderers);
        //?}
    }

    @Switch(
            title = "Enable FullBright"
    )
    public boolean enable = true;

    @Slider(
            title = "Light Level",
            description = "The light level everything is rendered at. Server-side logic such as mob spawning is unaffected.",
            max = 15f, step = 1f
    )
    public int lightLevel = 15;
}
