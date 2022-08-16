package com.redlimerl.tabfocus.mixins.accessor;

import net.minecraft.client.gui.screen.SoundsScreen;
import net.minecraft.client.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SoundsScreen.class)
public interface SoundsScreenAccessor {
    @Invoker
    String callGetVolume(SoundCategory soundCategory);
}
