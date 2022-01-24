package com.redlimerl.tabfocus.mixins.accessor;

import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameOptions.Option.class)
public interface GameOptionsOptionAccessor {
    @Accessor("step")
    float getStep();

    @Accessor("min")
    float getMin();
}
