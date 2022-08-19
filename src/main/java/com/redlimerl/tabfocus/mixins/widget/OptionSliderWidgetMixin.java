package com.redlimerl.tabfocus.mixins.widget;

import com.redlimerl.tabfocus.CoolGuyOptionSlider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_394;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOption;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(class_394.class)
public abstract class OptionSliderWidgetMixin extends ButtonWidget implements CoolGuyOptionSlider {

    @Shadow public float field_1281;

    @Shadow private GameOption field_1283;

    public OptionSliderWidgetMixin(int id, int x, int y, String message) {
        super(id, x, y, message);
    }

    @Override
    public void moveValue(boolean isLeft) {
        MinecraftClient client = MinecraftClient.getInstance();

        this.field_1281 = MathHelper.clamp(this.field_1281 + (isLeft ? -0.01f : 0.01f), 0.0F, 1.0F);

        client.options.setOption(this.field_1283, this.field_1281);
        this.message = client.options.getStringOption(this.field_1283);
    }
}
