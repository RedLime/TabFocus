package com.redlimerl.tabfocus.mixins.widget;

import com.redlimerl.tabfocus.CoolGuyOptionSlider;
import com.redlimerl.tabfocus.mixins.accessor.GameOptionsOptionAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OptionSliderWidget.class)
public abstract class OptionSliderWidgetMixin extends ButtonWidget implements CoolGuyOptionSlider {

    @Shadow private float value;

    @Shadow private GameOptions.Option option;

    public OptionSliderWidgetMixin(int id, int x, int y, String message) {
        super(id, x, y, message);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void moveValue(boolean isLeft) {
        MinecraftClient client = MinecraftClient.getInstance();
        GameOptionsOptionAccessor optionAccessor = (GameOptionsOptionAccessor) ((Object) this.option);

        float f;
        if (optionAccessor.getStep() == 0) {
            this.value = MathHelper.clamp(this.value + (isLeft ? -0.01f : 0.01f), 0.0F, 1.0F);
            f = this.option.getValue(this.value);
        } else {
            f = MathHelper.clamp(this.option.getValue(this.value) + (optionAccessor.getStep() * (isLeft ? -1 : 1)), optionAccessor.getMin(), this.option.getMaxValue());
        }
        client.options.setValue(this.option, f);
        this.value = this.option.getRatio(f);
        this.message = client.options.getValueMessage(this.option);
    }
}
