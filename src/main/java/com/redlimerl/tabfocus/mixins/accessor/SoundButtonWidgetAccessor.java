package com.redlimerl.tabfocus.mixins.accessor;

import net.minecraft.client.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.client.gui.screen.SoundsScreen$SoundButtonWidget")
public interface SoundButtonWidgetAccessor {
    @Accessor
    SoundCategory getCategory();

    @Accessor("field_20319")
    double getVolume();
    @Accessor("field_20319")
    void setVolume(double volume);

    @Accessor
    String getCategoryName();
}
