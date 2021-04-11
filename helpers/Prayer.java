package net.runelite.client.plugins.zulrah.helpers;

import lombok.SneakyThrows;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.zulrah.ZulrahPlugin;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Prayer {
    private ZulrahPlugin plugin;

    private final static int FKEY_PRAYERS = 113;

    private Robot robot;

    @SneakyThrows
    public Prayer(ZulrahPlugin plugin) {
        this.plugin = plugin;
        this.robot = new Robot();
    }

    public void enable(ArrayList<net.runelite.client.plugins.zulrah.enums.Prayer> prayers, boolean turnOffOthers) {
        pressKey(401, FKEY_PRAYERS);
        pressKey(402, FKEY_PRAYERS);
        //pressKey(400, FKEY_PRAYERS);

        ArrayList<net.runelite.client.plugins.zulrah.enums.Prayer> prayersToEnable = new ArrayList<>();
        ArrayList<net.runelite.client.plugins.zulrah.enums.Prayer> prayersToDisable = new ArrayList<>();

        for (int i = 0; i < prayers.size(); i++) {
            if (!plugin.getClient().isPrayerActive(prayers.get(i).getPrayer())) {
                prayersToEnable.add(prayers.get(i));
            }
        }

        for (int i = 0; i < net.runelite.client.plugins.zulrah.enums.Prayer.values().length; i++) {
            net.runelite.client.plugins.zulrah.enums.Prayer prayer = net.runelite.client.plugins.zulrah.enums.Prayer.values()[i];

            // disable if
            // 1. there's no prayer of the same category in the enable list
            // 2. the prayer is not in the enable list
            // 3. the prayer is active
            boolean doesCategoryExist = prayers.stream().filter(p -> p.getCategory() == prayer.getCategory()).toArray().length > 0;

            if (!doesCategoryExist && prayers.indexOf(prayer) == -1 && plugin.getClient().isPrayerActive(prayer.getPrayer())) {
                prayersToDisable.add(prayer);
            }
        }

        new Thread(() -> {
            plugin.setIsSwitchingPrayers(true);
            plugin.sleep(50);

            prayersToDisable.addAll(prayersToEnable);

            for (int i = 0; i < prayersToDisable.size(); i++) {
                Widget widget = plugin.getClient().getWidget(prayersToDisable.get(i).getWidgetInfo());

                int w = widget.getWidth();
                int h = widget.getHeight();


                plugin.getMouse().clickAt(
                        plugin.randomNumber(widget.getCanvasLocation().getX(), widget.getCanvasLocation().getX() + w),
                        plugin.randomNumber(widget.getCanvasLocation().getY(), widget.getCanvasLocation().getY() + h)
                );

                plugin.sleep(100);
            }

            plugin.sleep(500);
            plugin.setIsSwitchingPrayers(false);
        }).start();
    }

    public boolean areAllEnabled(ArrayList<net.runelite.client.plugins.zulrah.enums.Prayer> prayers) {
        for (int i = 0; i < prayers.size(); i++) {
            if (!plugin.getClient().isPrayerActive(prayers.get(i).getPrayer())) {
                return false;
            }
        }
        return true;
    }

    public boolean areAnyEnabledExcept(ArrayList<net.runelite.client.plugins.zulrah.enums.Prayer> prayers) {
        for (int i = 0; i < net.runelite.client.plugins.zulrah.enums.Prayer.values().length; i++) {
            net.runelite.client.plugins.zulrah.enums.Prayer prayer = net.runelite.client.plugins.zulrah.enums.Prayer.values()[i];

            if (!prayers.contains(prayer) && plugin.getClient().isPrayerActive(prayer.getPrayer())) {
                return true;
            }
        }

        return false;
    }

    private void pressKey(int id, int keycode) {
        KeyEvent e = new KeyEvent(
                plugin.getClient().getCanvas(), id, System.currentTimeMillis(), 0, keycode, KeyEvent.CHAR_UNDEFINED
        );

        plugin.getClient().getCanvas().dispatchEvent(e);
    }
}
