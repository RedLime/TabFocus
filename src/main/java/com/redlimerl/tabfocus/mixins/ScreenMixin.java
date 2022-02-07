package com.redlimerl.tabfocus.mixins;

import com.redlimerl.tabfocus.CoolGuyOptionSlider;
import com.redlimerl.tabfocus.CoolPeopleListWidget;
import com.redlimerl.tabfocus.FocusableWidget;
import net.minecraft.class_392;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.redlimerl.tabfocus.FocusableWidget.FOCUSED_WIDGET;
import static com.redlimerl.tabfocus.FocusableWidget.focusableWidgets;
import static com.redlimerl.tabfocus.TabFocus.FOCUSED_BUTTON_ORDER;

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
            if (keyCode == 15) {
                FocusableWidget.deselectWidget();
                while (focusableWidgets.stream().anyMatch(FocusableWidget::isSelectableWidget)) {
                    if (Screen.hasShiftDown()) {
                        FOCUSED_BUTTON_ORDER = (FOCUSED_BUTTON_ORDER - 1) % focusableWidgets.size();
                        if (FOCUSED_BUTTON_ORDER < 0) FOCUSED_BUTTON_ORDER = focusableWidgets.size() - 1;
                    }
                    else FOCUSED_BUTTON_ORDER = (FOCUSED_BUTTON_ORDER + 1) % focusableWidgets.size();
                    FocusableWidget<?> widget = FocusableWidget.get(FOCUSED_BUTTON_ORDER);
                    if (widget != null && widget.isSelectableWidget()) {
                        FocusableWidget.selectWidget(widget);
                        break;
                    }
                }
            }

            // Press Enter
            else if ((keyCode == 28 || keyCode == 156)) {
                ButtonWidget button = FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, ButtonWidget.class);
                if (button != null) {
                    button.playDownSound(this.client.getSoundManager());
                    this.buttonClicked(button);
                }
                class_392 worldList = FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, class_392.class);
                if (worldList != null) {
                    CoolPeopleListWidget widget = (CoolPeopleListWidget) worldList;
                    widget.clickElement();
                }
            }

            // Press left/right key (for Slider widget)
            else if (keyCode == 203 || keyCode == 205) {
                DrawableHelper sliderWidgetHelper = this.prevClickedButton != null ? this.prevClickedButton : FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, DrawableHelper.class);
                if (sliderWidgetHelper instanceof SliderWidget) {
                    SliderWidget sliderWidget = (SliderWidget) sliderWidgetHelper;
                    sliderWidget.method_9459(MathHelper.clamp(sliderWidget.getProgress() + (keyCode == 203 ? -0.01f : 0.01f), 0f, 1f));
                } else if (sliderWidgetHelper instanceof OptionSliderWidget) {
                    OptionSliderWidget sliderWidget = (OptionSliderWidget) sliderWidgetHelper;
                    ((CoolGuyOptionSlider) sliderWidget).moveValue(keyCode == 203);
                }
            }

            // Press up/down key (for Slider widget)
            else if ((keyCode == 200 || keyCode == 208) && FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, class_392.class) != null) {
                class_392 worldListWidget = FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, class_392.class);
                if (worldListWidget != null) {
                    CoolPeopleListWidget widget = (CoolPeopleListWidget) worldListWidget;
                    widget.moveElement(keyCode == 200);
                }
            }

            // Press ESC
            else if (keyCode == 1) {
                clear();
            }
        }
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init()V", shift = At.Shift.BEFORE))
    public void onInit(CallbackInfo ci) {
        clear();
    }

    public void clear() {
        FOCUSED_BUTTON_ORDER = -1;
        FOCUSED_WIDGET = null;
        focusableWidgets.clear();
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    public void onMouseClicked(CallbackInfo ci) {
        FOCUSED_BUTTON_ORDER = -1;
        FOCUSED_WIDGET = null;
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void onDebug(CallbackInfo ci) {
        //this.client.textRenderer.draw(Integer.toString(FOCUSED_BUTTON_ORDER), 10, 10, -1);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/MinecraftClient;II)V"))
    public void buttonRender(ButtonWidget instance, MinecraftClient client, int mouseX, int mouseY) {
        FocusableWidget.initWidget(instance, () -> instance.visible && instance.active);
        if (FOCUSED_WIDGET != null && FOCUSED_WIDGET.isEquals(instance)) {
            instance.render(client, instance.x, instance.y);
        } else {
            instance.render(client, mouseX, mouseY);
        }
    }
}
