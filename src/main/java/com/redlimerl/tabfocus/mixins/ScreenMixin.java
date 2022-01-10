package com.redlimerl.tabfocus.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
    @Shadow protected TextRenderer textRenderer;

    @Shadow protected abstract void buttonClicked(ButtonWidget button);

    @Shadow protected MinecraftClient client;

    @Shadow private ButtonWidget prevClickedButton;

    @Inject(method = "handleKeyboard", at = @At("HEAD"))
    public void onKeyPressed(CallbackInfo ci) {
        if (Keyboard.getEventKeyState()) {
            int keyCode = Keyboard.getEventKey();
            if (keyCode == 15
                    && drawableHelpers.stream().noneMatch(this::isHoveredButtonByMouse)
                    && drawableHelpers.stream().anyMatch(this::isSelectableWidget)) {
                while (true) {
                    FOCUSED_BUTTON_ORDER = (FOCUSED_BUTTON_ORDER + 1) % drawableHelpers.size();
                    DrawableHelper drawableHelper = drawableHelpers.get(FOCUSED_BUTTON_ORDER);
                    if (this.isSelectableWidget(drawableHelper)) {
                        FOCUSED_BUTTON_ID = getDrawableHelperId(drawableHelper);
                        this.prevClickedButton = null;
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
            else if ((keyCode == 28 || keyCode == 156) && FOCUSED_BUTTON_ORDER != -1) {
                DrawableHelper drawableHelper = drawableHelpers.get(FOCUSED_BUTTON_ORDER);
                if (drawableHelper instanceof ButtonWidget) {
                    ButtonWidget button = (ButtonWidget) drawableHelper;
                    this.prevClickedButton = button;
                    button.playDownSound(this.client.getSoundManager());
                    this.buttonClicked(button);
                }
            }
            else if (keyCode == 1) {
                FOCUSED_BUTTON_ORDER = -1;
            }
        }
    }

    @Inject(method = "handleMouse", at = @At("TAIL"))
    public void onHandleMouse(CallbackInfo ci) {
        if (drawableHelpers.stream().anyMatch(this::isHoveredButtonByMouse)) {
            FOCUSED_BUTTON_ORDER = -1;
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(CallbackInfo ci) {
        FOCUSED_BUTTON_ORDER = -1;
        FOCUSED_TEXT_FIELD = null;
        drawableHelpers.clear();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void onDebug(CallbackInfo ci) {
        this.textRenderer.draw(Boolean.toString(FOCUSED_BUTTON_ORDER == -1), 10, 10, -1);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/MinecraftClient;II)V"))
    public void buttonRender(ButtonWidget instance, MinecraftClient client, int mouseX, int mouseY) {
        if (FOCUSED_BUTTON_ORDER != -1 && instance.id == FOCUSED_BUTTON_ID) {
            instance.render(client, instance.x, instance.y);
        } else {
            instance.render(client, mouseX, mouseY);
        }
    }

    private boolean isHoveredButtonByMouse(DrawableHelper drawableHelper) {
        if (drawableHelper instanceof ButtonWidget) {
            ButtonWidget buttonWidget = (ButtonWidget) drawableHelper;
            return buttonWidget.isHovered() && buttonWidget.id != FOCUSED_BUTTON_ID;
        }
        return false;
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
