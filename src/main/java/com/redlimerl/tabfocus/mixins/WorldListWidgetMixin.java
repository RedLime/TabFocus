package com.redlimerl.tabfocus.mixins;

import com.redlimerl.tabfocus.CoolPeopleListWidget;
import com.redlimerl.tabfocus.FocusableWidget;
import net.minecraft.class_392;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_392.class)
public abstract class WorldListWidgetMixin implements CoolPeopleListWidget {

    @Shadow protected abstract int getEntryCount();

    @Shadow protected abstract void selectEntry(int index, boolean bl, int lastMouseX, int lastMouseY);

    private int lastIndex = -1;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void initWorldWidget(CallbackInfo ci) {
        FocusableWidget.initWidget(this, () -> this.getEntryCount() > 0,
                () -> this.selectEntry(0, false, 0, 0),
                () -> {});
    }

    @Inject(method = "selectEntry", at = @At("HEAD"))
    public void onSelect(int index, boolean bl, int lastMouseX, int lastMouseY, CallbackInfo ci) {
        lastIndex = index;
    }

    @Override
    public void moveElement(boolean isUp) {
        if (this.getEntryCount() == 0 && lastIndex == -1) return;
        int idx = lastIndex + (isUp ? -1 : 1);
        if (idx < 0) idx = this.getEntryCount() - 1;
        if (idx >= this.getEntryCount()) idx = 0;
        this.selectEntry(idx, false, 0, 0);
    }

    @Override
    public void clickElement() {
        if (this.getEntryCount() == 0 && lastIndex == -1) return;
        this.selectEntry(lastIndex, true, 0, 0);
    }
}
