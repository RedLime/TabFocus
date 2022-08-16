package com.redlimerl.tabfocus.mixins.widget;

import com.redlimerl.tabfocus.FocusableWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin extends DrawableHelper {
    @Shadow public abstract boolean isVisible();

    @Shadow public abstract boolean isFocused();

    @Shadow public abstract void setFocused(boolean focused);

    @Inject(method = "method_18385", at = @At("HEAD"))
    public void onRender(CallbackInfo ci) {
        FocusableWidget.initWidget(this, () -> this.isVisible() && !this.isFocused(),
                () -> this.setFocused(true),
                () -> this.setFocused(false));
    }
}
