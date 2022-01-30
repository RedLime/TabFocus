package com.redlimerl.tabfocus.mixins.widget;

import com.redlimerl.tabfocus.CoolGuyOptionSlider;
import com.redlimerl.tabfocus.mixins.accessor.GameOptionAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OptionSliderWidget.class)
public abstract class OptionSliderWidgetMixin extends ButtonWidget implements CoolGuyOptionSlider {

    @Shadow private float value;

    @Shadow private GameOption option;

    public OptionSliderWidgetMixin(int id, int x, int y, String message) {
        super(id, x, y, message);
    }

    @Override
    public void moveValue(boolean isLeft) {
        MinecraftClient client = MinecraftClient.getInstance();
        GameOptionAccessor optionAccessor = (GameOptionAccessor) this.option;

        float f;
        if (optionAccessor.getStep() == 0) {
            this.value = MathHelper.clamp(this.value + (isLeft ? -0.01f : 0.01f), 0.0F, 1.0F);
            f = this.option.method_6661(this.value);
        } else {
            f = MathHelper.clamp(this.option.method_6661(this.value) + (optionAccessor.getStep() * (isLeft ? -1 : 1)), optionAccessor.getMin(), this.option.method_6663());
        }
        client.options.method_869(this.option, f);
        this.value = this.option.method_6660(f);
        this.message = client.options.method_878(this.option);
    }
}
