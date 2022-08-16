package com.redlimerl.tabfocus.mixins;

import com.redlimerl.tabfocus.CoolGuyOptionSlider;
import com.redlimerl.tabfocus.CoolPeopleListWidget;
import com.redlimerl.tabfocus.FocusableWidget;
import com.redlimerl.tabfocus.mixins.accessor.SoundButtonWidgetAccessor;
import com.redlimerl.tabfocus.mixins.accessor.SoundsScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2848;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SoundsScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.redlimerl.tabfocus.FocusableWidget.FOCUSED_WIDGET;
import static com.redlimerl.tabfocus.FocusableWidget.focusableWidgets;
import static com.redlimerl.tabfocus.TabFocus.FOCUSED_BUTTON_ORDER;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow protected abstract void method_18847();
    private final MinecraftClient client = MinecraftClient.getInstance();

    @Inject(method = "keyPressed", at = @At("HEAD"))
    public void keyPressed(int i, int keyCode, int j, CallbackInfoReturnable<Boolean> cir) {
        // Press Tab
        if (keyCode == 15) {
            FocusableWidget.deselectWidget();
            while (focusableWidgets.stream().anyMatch(FocusableWidget::isSelectableWidget)) {
                if (Screen.hasShiftDown()) {
                    FOCUSED_BUTTON_ORDER = (FOCUSED_BUTTON_ORDER - 1) % focusableWidgets.size();
                    if (FOCUSED_BUTTON_ORDER < 0) FOCUSED_BUTTON_ORDER = focusableWidgets.size() - 1;
                } else FOCUSED_BUTTON_ORDER = (FOCUSED_BUTTON_ORDER + 1) % focusableWidgets.size();
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
                button.playDownSound(MinecraftClient.getInstance().getSoundManager());
                if (!(button instanceof OptionSliderWidget)) {
                    button.method_18374(button.x, button.y);
                }
            }

            class_2848 worldList = FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, class_2848.class);
            if (worldList != null) {
                CoolPeopleListWidget widget = (CoolPeopleListWidget) worldList;
                widget.clickElement();
            }
        }

        // Press left/right key (for Slider widget)
        else if (keyCode == 331 || keyCode == 333) {
            DrawableHelper sliderWidgetHelper = FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, DrawableHelper.class);
            if (sliderWidgetHelper instanceof OptionSliderWidget) {
                OptionSliderWidget sliderWidget = (OptionSliderWidget) sliderWidgetHelper;
                ((CoolGuyOptionSlider) sliderWidget).moveValue(keyCode == 331);
            } else if (client.currentScreen instanceof SoundsScreen && sliderWidgetHelper != null && ((ButtonWidget) sliderWidgetHelper).id != 200) {
                SoundButtonWidgetAccessor slider = ((SoundButtonWidgetAccessor) sliderWidgetHelper);
                slider.setVolume(Math.min(1, Math.max(0, slider.getVolume() + (keyCode == 331 ? -0.01f : 0.01f))));
                client.options.setSoundVolume(slider.getCategory(), slider.getVolume());
                client.options.save();
                ((ButtonWidget) sliderWidgetHelper).message = slider.getCategoryName() + ": " + ((SoundsScreenAccessor) client.currentScreen).callGetVolume(slider.getCategory());
            }
        }

        // Press up/down key (for Slider widget)
        else if ((keyCode == 328 || keyCode == 336) && FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, class_2848.class) != null) {
            class_2848 worldListWidget = FocusableWidget.getWidgetOrNull(FOCUSED_BUTTON_ORDER, class_2848.class);
            if (worldListWidget != null) {
                CoolPeopleListWidget widget = (CoolPeopleListWidget) worldListWidget;
                widget.moveElement(keyCode == 328);
            }
        }

        // Press ESC
        else if (keyCode == 1) {
            clear();
        }
    }

    public void clear() {
        FOCUSED_BUTTON_ORDER = -1;
        FOCUSED_WIDGET = null;
        focusableWidgets.clear();
    }

    @Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;method_18847()V"))
    public void buttonClickedRedirect(CreateWorldScreen instance) {
        if (!(MinecraftClient.getInstance().currentScreen instanceof CreateWorldScreen)) return;
        if (FocusableWidget.FOCUSED_WIDGET != null) {
            if (FocusableWidget.FOCUSED_WIDGET.is(TextFieldWidget.class)) {
                this.method_18847();
            }
        } else {
            this.method_18847();
        }
    }
}
