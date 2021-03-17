package net.runelite.client.plugins.zulrah.helpers;

import lombok.SneakyThrows;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.zulrah.ZulrahPlugin;

import java.awt.*;

public class Mouse {
    private final Robot robot;
    private final ZulrahPlugin plugin;

    @SneakyThrows
    public Mouse(ZulrahPlugin plugin) {
        this.robot = new Robot();
        this.plugin = plugin;
    }

    public void clickAt(int x, int y) {
        System.out.println("click at " + x + ", " + y);

        new Thread(() -> {
            robot.mouseMove(
                    x + plugin.getScreenOffsetX(),
                    y + plugin.getScreenOffsetY()
            );

            plugin.sleep(20);
            click();
        }).start();
    }

    public void click() {
        robot.mousePress(1024);
        robot.mouseRelease(1024);
    }

    public void clickLocalPoint(LocalPoint localPoint, boolean useMap) {
        Polygon poly = Perspective.getCanvasTilePoly(plugin.getClient(), localPoint);

        if (useMap) {
            Point p = Perspective.localToMinimap(plugin.getClient(), localPoint);

            clickAt(
                    plugin.randomNumber(p.getX(), p.getX() + 2),
                    plugin.randomNumber(p.getY(), p.getY() + 2)
            );
        } else {
            int x = (int) poly.getBounds2D().getX();
            int y = (int) poly.getBounds2D().getY();
            int w = (int) poly.getBounds().getWidth();
            int h = (int) poly.getBounds2D().getHeight();

            clickAt(
                    plugin.randomNumber(x, x + w),
                    plugin.randomNumber(y, y + h)
            );
        }
    }

    public void clickNpc(NPC npc) {
        final Polygon tile = Perspective.getCanvasTilePoly(plugin.getClient(), npc.getLocalLocation());

        int x = (int) tile.getBounds().getCenterX();
        int y = (int) tile.getBounds().getCenterY();

        x = plugin.randomNumber(x - 5, x + 5);
        y = plugin.randomNumber(y - 5, y + 5);

        clickAt(x, y);
    }
}
