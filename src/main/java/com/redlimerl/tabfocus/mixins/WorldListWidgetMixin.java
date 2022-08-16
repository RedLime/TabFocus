package com.redlimerl.tabfocus.mixins;

import com.redlimerl.tabfocus.CoolPeopleListWidget;
import com.redlimerl.tabfocus.FocusableWidget;
import com.redlimerl.tabfocus.mixins.accessor.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.class_2847;
import net.minecraft.client.class_2848;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_2848.class)
public abstract class WorldListWidgetMixin extends EntryListWidget implements CoolPeopleListWidget {

    public WorldListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    @Shadow public abstract void method_12214(int i);

    private int lastIndex = -1;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initWorldWidget(CallbackInfo ci) {
        this.getEntryCount();
        FocusableWidget.initWidget(this, () -> this.getEntryCount() > 0,
                () -> this.method_12214(0),
                () -> {});
    }

    @Inject(method = "method_12214", at = @At("HEAD"))
    public void onSelect(int index, CallbackInfo ci) {
        lastIndex = index;
    }

    @Override
    public void moveElement(boolean isUp) {
        if (this.getEntryCount() == 0 && lastIndex == -1) return;
        int idx = lastIndex + (isUp ? -1 : 1);
        if (idx < 0) idx = this.getEntryCount() - 1;
        if (idx >= this.getEntryCount()) idx = 0;
        this.method_12214(idx);
    }

    @Override
    public void clickElement() {
        if (this.getEntryCount() == 0 && lastIndex == -1) return;
        ((class_2847)((EntryListWidgetAccessor)this).callGetEntry(lastIndex)).method_12202();
    }
}
