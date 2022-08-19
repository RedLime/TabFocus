package com.redlimerl.tabfocus.mixins;

import com.redlimerl.tabfocus.CoolPeopleListWidget;
import com.redlimerl.tabfocus.FocusableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/client/class_392")
abstract class WorldListWidgetMixin implements CoolPeopleListWidget {

    @Shadow protected abstract int getEntryCount();

    @Shadow protected abstract void method_1057(int index, boolean bl);

    private int lastIndex = -1;

    @Inject(method = "<init>(Lnet/minecraft/client/gui/screen/world/SelectWorldScreen;)V", at = @At("TAIL"))
    public void initWorldWidget(CallbackInfo ci) {
        FocusableWidget.initWidget(this, () -> this.getEntryCount() > 0,
                () -> this.method_1057(0, false),
                () -> {});
    }

    @Inject(method = "method_1057", at = @At("HEAD"))
    public void onSelect(int index, boolean par2, CallbackInfo ci) {
        lastIndex = index;
    }

    @Override
    public void moveElement(boolean isUp) {
        if (this.getEntryCount() == 0 && lastIndex == -1) return;
        int idx = lastIndex + (isUp ? -1 : 1);
        if (idx < 0) idx = this.getEntryCount() - 1;
        if (idx >= this.getEntryCount()) idx = 0;
        this.method_1057(idx, false);
    }

    @Override
    public void clickElement() {
        if (this.getEntryCount() == 0 && lastIndex == -1) return;
        this.method_1057(lastIndex, true);
    }
}
