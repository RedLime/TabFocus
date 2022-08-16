package com.redlimerl.tabfocus.mixins.accessor;

import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameOptions.Option.class)
public interface GameOptionsOptionAccessor {
    @Accessor("field_19996")
    float getStep();

    @Accessor("field_19997")
    double getMin();
}
