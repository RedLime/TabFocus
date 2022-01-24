package com.redlimerl.tabfocus.mixins;

import com.redlimerl.tabfocus.CoolGuyOptionSlider;
import com.redlimerl.tabfocus.TabFocus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.redlimerl.tabfocus.TabFocus.*;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow protected abstract void buttonClicked(ButtonWidget button);

    @Shadow protected MinecraftClient client;

    @Shadow private ButtonWidget prevClickedButton;

    @Inject(method = "handleKeyboard", at = @At("HEAD"))
    public void onKeyPressed(CallbackInfo ci) {
        if (Keyboard.getEventKeyState()) {
            int keyCode = Keyboard.getEventKey();

            // Press Tab
            if (keyCode == 15
                    && drawableHelpers.stream().anyMatch(this::isSelectableWidget)) {
                while (true) {
                    if (Screen.hasShiftDown()) {
                        FOCUSED_BUTTON_ORDER = (FOCUSED_BUTTON_ORDER - 1) % drawableHelpers.size();
                        if (FOCUSED_BUTTON_ORDER < 0) FOCUSED_BUTTON_ORDER = drawableHelpers.size() - 1;
                    }
                    else FOCUSED_BUTTON_ORDER = (FOCUSED_BUTTON_ORDER + 1) % drawableHelpers.size();
                    DrawableHelper drawableHelper = drawableHelpers.get(FOCUSED_BUTTON_ORDER);
                    if (this.isSelectableWidget(drawableHelper)) {
                        FOCUSED_BUTTON_ID = getDrawableHelperId(drawableHelper);
                        if (FOCUSED_TEXT_FIELD != null) {
                            FOCUSED_TEXT_FIELD.setFocused(false);
                        }
                        if (drawableHelper instanceof TextFieldWidget) {
                            TextFieldWidget textFieldWidget = (TextFieldWidget) drawableHelper;
                            textFieldWidget.setFocused(true);
                            FOCUSED_TEXT_FIELD = textFieldWidget;
                        }
                        break;
                    }
                }
            }

            // Press Enter
            else if ((keyCode == 28 || keyCode == 156) && FOCUSED_BUTTON_ORDER != -1) {
                DrawableHelper drawableHelper = drawableHelpers.get(FOCUSED_BUTTON_ORDER);
                if (drawableHelper instanceof ButtonWidget) {
                    ButtonWidget button = (ButtonWidget) drawableHelper;
                    button.playDownSound(this.client.getSoundManager());
                    this.buttonClicked(button);
                }
            }

            // Press allow key (for Slider widget)
            else if ((keyCode == 203 || keyCode == 205) && FOCUSED_BUTTON_ORDER != -1) {
                DrawableHelper drawableHelper = this.prevClickedButton != null ? this.prevClickedButton : drawableHelpers.get(FOCUSED_BUTTON_ORDER);
                if (drawableHelper instanceof OptionSliderWidget) {
                    OptionSliderWidget sliderWidget = (OptionSliderWidget) drawableHelper;
                    ((CoolGuyOptionSlider) sliderWidget).moveValue(keyCode == 203);
                }
            }

            // Press ESC
            else if (keyCode == 1) {
                FOCUSED_BUTTON_ORDER = -1;
            }
        }
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        FOCUSED_BUTTON_ORDER = -1;
        FOCUSED_TEXT_FIELD = null;
        FOCUSED_BUTTON_ID = -1;
        drawableHelpers.clear();
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    public void onMouseClicked(CallbackInfo ci) {
        FOCUSED_BUTTON_ORDER = -1;
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void onDebug(CallbackInfo ci) {
        //this.client.textRenderer.draw(Integer.toString(FOCUSED_BUTTON_ORDER), 10, 10, -1);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/MinecraftClient;II)V"))
    public void buttonRender(ButtonWidget instance, MinecraftClient client, int mouseX, int mouseY) {
        if (!TabFocus.drawableHelpers.contains(instance)) TabFocus.drawableHelpers.add(instance);
        if (FOCUSED_BUTTON_ORDER != -1 && instance.id == FOCUSED_BUTTON_ID) {
            instance.render(client, instance.x, instance.y);
        } else {
            instance.render(client, mouseX, mouseY);
        }
    }

    private boolean isSelectableWidget(DrawableHelper drawableHelper) {
        if (drawableHelper instanceof ButtonWidget) {
            ButtonWidget buttonWidget = (ButtonWidget) drawableHelper;
            return buttonWidget.visible && buttonWidget.active;
        }
        if (drawableHelper instanceof TextFieldWidget) {
            TextFieldWidget textFieldWidget = (TextFieldWidget) drawableHelper;
            return textFieldWidget.isVisible() && !textFieldWidget.isFocused();
        }
        return false;
    }

    private int getDrawableHelperId(DrawableHelper drawableHelper) {
        if (drawableHelper instanceof ButtonWidget) {
            ButtonWidget buttonWidget = (ButtonWidget) drawableHelper;
            return buttonWidget.id;
        }
        if (drawableHelper instanceof TextFieldWidget) {
            return -2;
        }
        return -1;
    }
}
