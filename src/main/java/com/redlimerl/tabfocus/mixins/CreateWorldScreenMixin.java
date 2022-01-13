package com.redlimerl.tabfocus.mixins;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.redlimerl.tabfocus.TabFocus.FOCUSED_BUTTON_ORDER;
import static com.redlimerl.tabfocus.TabFocus.drawableHelpers;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow protected abstract void buttonClicked(ButtonWidget button);

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;buttonClicked(Lnet/minecraft/client/gui/widget/ButtonWidget;)V"))
    public void buttonClickedRedirect(CreateWorldScreen instance, ButtonWidget button) {
        if (drawableHelpers.size() > 0 && (FOCUSED_BUTTON_ORDER == -1 || drawableHelpers.get(FOCUSED_BUTTON_ORDER) instanceof TextFieldWidget)) {
            this.buttonClicked(button);
        }
    }
}
