package com.redlimerl.tabfocus.mixins.accessor;

import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameOption.class)
public interface GameOptionsOptionAccessor {
    @Accessor("field_7688")
    float getStep();

    @Accessor("field_7689")
    float getMin();
}
