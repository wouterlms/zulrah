package net.runelite.client.plugins.zulrah.enums;

import net.runelite.api.widgets.WidgetInfo;

public enum Prayer {
    EAGLE_EYE(WidgetInfo.PRAYER_EAGLE_EYE, net.runelite.api.Prayer.EAGLE_EYE, 0),
    MYSTIC_MIGHT(WidgetInfo.PRAYER_MYSTIC_MIGHT, net.runelite.api.Prayer.MYSTIC_MIGHT, 0),
    PROTECTED_FROM_RANGED(WidgetInfo.PRAYER_PROTECT_FROM_MISSILES, net.runelite.api.Prayer.PROTECT_FROM_MISSILES, 1),
    PROTECTED_FROM_MAGIC(WidgetInfo.PRAYER_PROTECT_FROM_MAGIC, net.runelite.api.Prayer.PROTECT_FROM_MAGIC, 1);

    private WidgetInfo widgetInfo;
    private net.runelite.api.Prayer prayer;
    private int category;

    Prayer(WidgetInfo widgetInfo, net.runelite.api.Prayer prayer, int category) {
        this.widgetInfo = widgetInfo;
        this.prayer = prayer;
        this.category = category;
    }

    public WidgetInfo getWidgetInfo() {
        return widgetInfo;
    }
    public net.runelite.api.Prayer getPrayer() {
        return prayer;
    }
    public int getCategory() { return category; }
}
