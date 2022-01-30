package com.redlimerl.tabfocus.mixins;

import com.redlimerl.tabfocus.FocusableWidget;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow protected abstract void buttonClicked(ButtonWidget button);

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;buttonClicked(Lnet/minecraft/client/gui/widget/ButtonWidget;)V"))
    public void buttonClickedRedirect(CreateWorldScreen instance, ButtonWidget button) {
        if (FocusableWidget.FOCUSED_WIDGET != null && FocusableWidget.FOCUSED_WIDGET.isEquals(button)) {
            this.buttonClicked(button);
        }
    }
}
