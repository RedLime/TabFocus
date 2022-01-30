package com.redlimerl.tabfocus.mixins.widget;

import com.redlimerl.tabfocus.FocusableWidget;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.redlimerl.tabfocus.FocusableWidget.FOCUSED_WIDGET;

@Mixin(ButtonWidget.class)
public abstract class ButtonWidgetMixin extends DrawableHelper {

    @Shadow public int x;

    @Shadow public int y;

    @Shadow public boolean visible;

    @Shadow public boolean active;

    @Inject(method = "render", at = @At("HEAD"))
    public void onRender(CallbackInfo ci) {
        FocusableWidget.initWidget(this, () -> this.visible && this.active);
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public int renderMouseX(int mouseX) {
        return FOCUSED_WIDGET != null && FOCUSED_WIDGET.isEquals(this) ? this.x : mouseX;
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    public int renderMouseY(int mouseY) {
        return FOCUSED_WIDGET != null && FOCUSED_WIDGET.isEquals(this) ? this.y : mouseY;
    }
}
