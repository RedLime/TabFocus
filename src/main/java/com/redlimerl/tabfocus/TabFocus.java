package com.redlimerl.tabfocus;


import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.ArrayList;

public class TabFocus implements ClientModInitializer {

    public static int FOCUSED_BUTTON_ORDER = -1;
    public static int FOCUSED_BUTTON_ID = -1;
    public static TextFieldWidget FOCUSED_TEXT_FIELD = null;
    public static ArrayList<DrawableHelper> drawableHelpers = new ArrayList<>();

    @Override
    public void onInitializeClient() {

    }
}
