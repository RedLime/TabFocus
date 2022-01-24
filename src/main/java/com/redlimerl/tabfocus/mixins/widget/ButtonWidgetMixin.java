package com.redlimerl.tabfocus.mixins.widget;

import com.redlimerl.tabfocus.TabFocus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.redlimerl.tabfocus.TabFocus.FOCUSED_BUTTON_ID;
import static com.redlimerl.tabfocus.TabFocus.FOCUSED_BUTTON_ORDER;

@Mixin(ButtonWidget.class)
public class ButtonWidgetMixin extends DrawableHelper {
    @Shadow public int id;

    @Shadow public int x;

    @Shadow public int y;

    @Inject(method = "render", at = @At("HEAD"))
    public void onRender(CallbackInfo ci) {
        if (!TabFocus.drawableHelpers.contains(this)) TabFocus.drawableHelpers.add(this);
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    public int renderMouseX(int mouseX) {
        return FOCUSED_BUTTON_ORDER != -1 && this.id == FOCUSED_BUTTON_ID ? this.x : mouseX;
    }

    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    public int renderMouseY(int mouseY) {
        return FOCUSED_BUTTON_ORDER != -1 && this.id == FOCUSED_BUTTON_ID ? this.y : mouseY;
    }
}
