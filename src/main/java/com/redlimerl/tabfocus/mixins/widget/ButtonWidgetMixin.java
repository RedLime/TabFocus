package com.redlimerl.tabfocus.mixins.widget;

import com.redlimerl.tabfocus.TabFocus;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ButtonWidget.class)
public abstract class ButtonWidgetMixin extends DrawableHelper {
    @Inject(method = "render", at = @At("HEAD"))
    public void onRender(CallbackInfo ci) {
        if (!TabFocus.drawableHelpers.contains(this)) TabFocus.drawableHelpers.add(this);
    }
}
