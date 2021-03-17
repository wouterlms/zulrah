package net.runelite.client.plugins.zulrah.enums;

import net.runelite.api.coords.LocalPoint;

public enum ZulrahPosition {
    CENTER(new LocalPoint(6720, 7616)),
    WEST(new LocalPoint(8000, 7360)),
    EAST(new LocalPoint(5440, 7360)),
    NORTH(new LocalPoint(6720, 6208));

    private LocalPoint position;

    public LocalPoint getPosition() {
        return this.position;
    }

    private ZulrahPosition(LocalPoint position) {
        this.position = position;
    }
}
