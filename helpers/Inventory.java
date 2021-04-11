package net.runelite.client.plugins.zulrah.helpers;

import lombok.SneakyThrows;
import net.runelite.api.ItemComposition;
import net.runelite.api.Point;
import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.plugins.zulrah.ZulrahPlugin;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class Inventory {
    private ZulrahPlugin plugin;
    private final int INVENTORY_SIZE = 28;
    private Robot robot;

    private static final int FKEY_INVENTORY = 27;

    @SneakyThrows
    public Inventory(ZulrahPlugin plugin) {
        this.plugin = plugin;
        this.robot = new Robot();
    }

    private WidgetItem getItemInSlot(int index)
    {
        Widget inventoryWidget = plugin.getClient().getWidget(WidgetInfo.INVENTORY);
        return inventoryWidget.getWidgetItem(index);
    }

    public WidgetItem getItem(final int itemID)
    {
        for (int i = 0; i < INVENTORY_SIZE; i++)
        {
            WidgetItem currentItem = getItemInSlot(i);

            if (currentItem.getId() == itemID)
            {
                return currentItem;
            }
        }
        return null;
    }

    public boolean containsAnyOf(String[] items) {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            WidgetItem item = getItemInSlot(i);
            ItemComposition itemComposition = plugin.getItemManager().getItemComposition(item.getId());

            if (Arrays.asList(items).contains(itemComposition.getName())) {
                return true;
            }
        }

        return false;
    }

    public boolean contains(String item) {
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            WidgetItem widgetItem = getItemInSlot(i);
            ItemComposition itemComposition = plugin.getItemManager().getItemComposition(widgetItem.getId());

            if (item.equals(itemComposition.getName())) {
                return true;
            }
        }

        return false;
    }

    public void clickItem(String item) {
        int slotToClick = -1;

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            WidgetItem widgetItem = getItemInSlot(i);
            ItemComposition itemComposition = plugin.getItemManager().getItemComposition(widgetItem.getId());

            if (item.equals(itemComposition.getName())) {
                slotToClick = i;
                break;
            }
        }

        if (slotToClick != -1) {
            int finalSlotToClick = slotToClick;

            new Thread(() -> {
                plugin.setIsEating(true);

                clickSlot(finalSlotToClick);

                plugin.sleep(600);
                plugin.setIsEating(false);
            }).start();
        }
    }

    public void clickItems(String[] items) {
        ArrayList<Integer> slotsToClick = new ArrayList<>();

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            WidgetItem item = getItemInSlot(i);
            ItemComposition itemComposition = plugin.getItemManager().getItemComposition(item.getId());

            if (Arrays.asList(items).contains(itemComposition.getName())) {
                slotsToClick.add(i);
            }
        }

        if (slotsToClick.size() > 0) {
            new Thread(() -> {
                plugin.setIsSwitchingGear(true);

                for (int i = 0; i < slotsToClick.size(); i++) {
                    clickSlot(slotsToClick.get(i));
                    plugin.sleep(100);
                }

                plugin.sleep(600);
                plugin.setIsSwitchingGear(false);
            }).start();
        }
    }

    public void clickFirstWhichContains(String item) {
        int slotToClick = -1;

        for (int i = 0; i < INVENTORY_SIZE; i++) {
            WidgetItem widgetItem = getItemInSlot(i);
            ItemComposition itemComposition = plugin.getItemManager().getItemComposition(widgetItem.getId());

            if (itemComposition.getName().contains(item)) {
                slotToClick = i;
                break;
            }
        }

        if (slotToClick != -1) {
            int finalSlotToClick = slotToClick;

            new Thread(() -> {
                plugin.setIsEating(true);

                clickSlot(finalSlotToClick);

                plugin.sleep(600);
                plugin.setIsEating(false);
            }).start();
        }
    }

    private void clickSlot(final int slot)
    {
        if (plugin.getClient().getVar(VarClientInt.INVENTORY_TAB) != 3) {
            pressKey(401, FKEY_INVENTORY);
            pressKey(402, FKEY_INVENTORY);
        }

        Widget inventoryWidget = plugin.getClient().getWidget(WidgetInfo.INVENTORY);
        WidgetItem item = inventoryWidget.getWidgetItem(slot);

        if (item == null) {
            return;
        }

        Point canvasLocation = item.getCanvasLocation();

        final int width = (int)item.getCanvasBounds().getWidth();
        final int height = (int)item.getCanvasBounds().getHeight();

        int x = canvasLocation.getX();
        int y = canvasLocation.getY();

        x = plugin.randomNumber(x + 10, x + width - 10);
        y = plugin.randomNumber(y + 10, y + height - 10);

        plugin.getMouse().clickAt(x, y);
    }

    private void pressKey(int id, int keycode) {
        KeyEvent e = new KeyEvent(
                plugin.getClient().getCanvas(), id, System.currentTimeMillis(), 0, keycode, KeyEvent.CHAR_UNDEFINED
        );

        plugin.getClient().getCanvas().dispatchEvent(e);
    }
}
