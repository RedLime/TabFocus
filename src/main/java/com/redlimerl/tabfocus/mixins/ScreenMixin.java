package com.redlimerl.tabfocus.mixins;

import com.redlimerl.tabfocus.CoolGuyOptionSlider;
import com.redlimerl.tabfocus.CoolPeopleListWidget;
import com.redlimerl.tabfocus.FocusableWidget;
import com.redlimerl.tabfocus.mixins.accessor.ControlsOptionsScreenAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.class_394;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ListWidget;
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

    @Shadow protected Minecraft field_1229;

    @Shadow private ButtonWidget prevClickedButton;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "method_1040", at = @At("HEAD"), cancellable = true)
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
                    if (field_1229.currentScreen instanceof ControlsOptionsScreen && ((ControlsOptionsScreenAccessor) field_1229.currentScreen).getSelectedKeyBinding() == -1) {
                        this.buttonClicked(button);
                        if (field_1229.currentScreen instanceof ControlsOptionsScreen && ((ControlsOptionsScreenAccessor) field_1229.currentScreen).getSelectedKeyBinding() != -1) {
                            ci.cancel();
                        } else {
                            this.field_1229.soundSystem.playSound("random.click", 1.0F, 1.0F);
                        }
                        return;
                    }
                    this.field_1229.soundSystem.playSound("random.click", 1.0F, 1.0F);
                    this.buttonClicked(button);
                }
                ListWidget worldList = FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, ListWidget.class);
                if (worldList != null && SelectWorldScreen.class.isInstance(this)) {
                    CoolPeopleListWidget widget = (CoolPeopleListWidget) worldList;
                    widget.clickElement();
                }
            }

            // Press allow key (for Slider widget)
            else if ((keyCode == 203 || keyCode == 205) && FOCUSED_BUTTON_ORDER != -1) {
                DrawableHelper sliderWidgetHelper = this.prevClickedButton != null ? this.prevClickedButton : FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, DrawableHelper.class);
                if (sliderWidgetHelper instanceof class_394) {
                    class_394 sliderWidget = (class_394) sliderWidgetHelper;
                    ((CoolGuyOptionSlider) sliderWidget).moveValue(keyCode == 203);
                }
            }

            // Press up/down key (for Slider widget)
            else if ((keyCode == 200 || keyCode == 208) && FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, ListWidget.class) != null) {
                ListWidget worldListWidget = FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, ListWidget.class);
                if (worldListWidget != null && SelectWorldScreen.class.isInstance(this)) {
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

    @Inject(method = "method_1028(Lnet/minecraft/client/Minecraft;II)V", at = @At("HEAD"))
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

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;method_891(Lnet/minecraft/client/Minecraft;II)V"))
    public void buttonRender(ButtonWidget instance, Minecraft client, int mouseX, int mouseY) {
        FocusableWidget.initWidget(instance, () -> instance.visible && instance.active);
        if (FOCUSED_WIDGET != null && FOCUSED_WIDGET.isEquals(instance)) {
            instance.method_891(client, instance.x, instance.y);
        } else {
            instance.method_891(client, mouseX, mouseY);
        }
    }
}
