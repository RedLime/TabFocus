package com.redlimerl.tabfocus.mixins.accessor;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntryListWidget.class)
public interface EntryListWidgetAccessor {
    @Invoker EntryListWidget.Entry callGetEntry(int index);
}
