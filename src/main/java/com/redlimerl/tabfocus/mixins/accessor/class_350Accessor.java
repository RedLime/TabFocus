package com.redlimerl.tabfocus.mixins.accessor;

import net.minecraft.class_350;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_350.class)
public interface class_350Accessor {
    @Accessor("field_7688")
    float getStep();

    @Accessor("field_7689")
    float getMin();
}
