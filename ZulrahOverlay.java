package net.runelite.client.plugins.zulrah;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.client.plugins.zulrah.enums.StandPosition;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class ZulrahOverlay extends Overlay {
    @Inject
    private Client client;

    private ZulrahPlugin plugin;

    private Color[] colors = new Color[9];

    @Inject
    private ZulrahOverlay(ZulrahPlugin plugin) {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.MED);

        colors[0] = Color.RED;
        colors[2] = Color.GREEN;
        colors[3] = Color.ORANGE;
        colors[4] = Color.BLUE;
        colors[5] = Color.CYAN;
        colors[5] = Color.DARK_GRAY;
        colors[6] = Color.MAGENTA;
        colors[7] = Color.YELLOW;
        colors[8] = Color.WHITE;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        final Polygon[] positions = new Polygon[9];

        positions[0] = Perspective.getCanvasTilePoly(client, StandPosition.SOUTH_WEST_CORNER.getPosition());
        positions[1] = Perspective.getCanvasTilePoly(client, StandPosition.SOUTH_WEST_CORNER_MELEE.getPosition());
        positions[2] = Perspective.getCanvasTilePoly(client, StandPosition.WEST_PILLAR.getPosition());
        positions[3] = Perspective.getCanvasTilePoly(client, StandPosition.WEST_PILLAR_NORTH.getPosition());
        positions[4] = Perspective.getCanvasTilePoly(client, StandPosition.EAST_PILLAR.getPosition());
        positions[5] = Perspective.getCanvasTilePoly(client, StandPosition.EAST_PILLAR_NORTH.getPosition());
        positions[6] = Perspective.getCanvasTilePoly(client, StandPosition.SOUTH_EAST.getPosition());
        positions[7] = Perspective.getCanvasTilePoly(client, StandPosition.SOUTH_EAST_MELEE.getPosition());
        positions[8] = Perspective.getCanvasTilePoly(client, StandPosition.MIDDLE.getPosition());

        for (int i = 0; i < positions.length; i++) {
            if (positions[i] != null) {
                OverlayUtil.renderPolygon(graphics, positions[i], colors[i]);
            }
        }

        return null;
    }
}
