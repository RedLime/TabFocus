package com.redlimerl.tabfocus.mixins.accessor;

import net.minecraft.client.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.class_1816")
public interface SoundButtonWidgetAccessor {
    @Accessor("field_7780")
    SoundCategory getCategory();

    @Accessor("field_7777")
    float getVolume();
    @Accessor("field_7777")
    void setVolume(float volume);

    @Accessor("field_7781")
    String getCategoryName();
}
