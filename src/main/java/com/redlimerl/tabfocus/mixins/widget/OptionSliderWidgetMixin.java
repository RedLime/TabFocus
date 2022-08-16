package com.redlimerl.tabfocus.mixins.widget;

import com.redlimerl.tabfocus.CoolGuyOptionSlider;
import com.redlimerl.tabfocus.mixins.accessor.GameOptionsOptionAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionSliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OptionSliderWidget.class)
public abstract class OptionSliderWidgetMixin extends ButtonWidget implements CoolGuyOptionSlider {

    @Shadow private double field_20084;

    @Final @Shadow private GameOptions.Option option;

    public OptionSliderWidgetMixin(int id, int x, int y, String message) {
        super(id, x, y, message);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void moveValue(boolean isLeft) {
        MinecraftClient client = MinecraftClient.getInstance();
        GameOptionsOptionAccessor optionAccessor = (GameOptionsOptionAccessor) ((Object) this.option);

        double f;
        if (optionAccessor.getStep() == 0) {
            this.field_20084 = MathHelper.clamp(this.field_20084 + (isLeft ? -0.01f : 0.01f), 0.0F, 1.0F);
            f = this.option.method_18263(this.field_20084);
        } else {
            f = MathHelper.clamp(this.option.method_18263(this.field_20084) + (optionAccessor.getStep() * (isLeft ? -1 : 1)), optionAccessor.getMin(), this.option.method_18267());
        }
        client.options.method_18257(this.option, f);
        this.field_20084 = this.option.method_18261(f);
        this.message = client.options.method_18260(this.option);
    }
}
