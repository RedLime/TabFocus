package com.redlimerl.tabfocus;

import java.util.ArrayList;
import java.util.function.Supplier;

public class FocusableWidget<T> {
    public static ArrayList<FocusableWidget<?>> focusableWidgets = new ArrayList<>();
    public static FocusableWidget<?> FOCUSED_WIDGET = null;

    public static <T> void initWidget(T obj, Supplier<Boolean> selectable) {
        if (focusableWidgets.stream().noneMatch(focusableWidget -> focusableWidget.isEquals(obj)))
            focusableWidgets.add(new FocusableWidget<>(obj, selectable, () -> {}, () -> {}));
    }

    public static <T> void initWidget(T obj, Supplier<Boolean> selectable, Runnable onSelect, Runnable onDeselect) {
        if (focusableWidgets.stream().noneMatch(focusableWidget -> focusableWidget.isEquals(obj)))
            focusableWidgets.add(new FocusableWidget<>(obj, selectable, onSelect, onDeselect));
    }

    public static FocusableWidget<?> get(int i) {
        if (i < 0 || i >= focusableWidgets.size()) return null;
        return focusableWidgets.get(i);
    }

    public static <T> T getWidgetOrNull(int i, Class<T> clazz) {
        FocusableWidget<?> widget = get(i);
        return widget == null ? null : widget.getOrNull(clazz);
    }

    public static void deselectWidget() {
        if (FOCUSED_WIDGET != null) {
            FOCUSED_WIDGET.deselect();
        }
    }

    public static void selectWidget(FocusableWidget<?> focusableWidget) {
        FOCUSED_WIDGET = focusableWidget;
        FOCUSED_WIDGET.select();
    }

    private final T obj;
    private final Supplier<Boolean> selectable;
    private final Runnable onSelect;
    private final Runnable onDeselect;

    public FocusableWidget(T obj, Supplier<Boolean> selectable, Runnable onSelect, Runnable onDeselect) {
        this.obj = obj;
        this.selectable = selectable;
        this.onSelect = onSelect;
        this.onDeselect = onDeselect;
    }

    public boolean is(Class<?> clazz) {
        return clazz.isInstance(obj);
    }

    @SuppressWarnings("unchecked")
    public <E> E getOrNull(Class<E> clazz) {
        return is(clazz) ? (E) obj : null;
    }

    public boolean isEquals(Object obj) {
        return obj.equals(this.obj);
    }

    public boolean isSelectableWidget() {
        return selectable.get();
    }

    public void select() {
        onSelect.run();
    }

    public void deselect() {
        onDeselect.run();
    }
}
