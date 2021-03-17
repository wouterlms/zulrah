package net.runelite.client.plugins.zulrah.enums;

import net.runelite.api.coords.LocalPoint;

public enum StandPosition {
    SOUTH_WEST_CORNER(new LocalPoint(7488, 7872)),
    SOUTH_WEST_CORNER_MELEE(new LocalPoint(7232, 8000)),
    WEST_PILLAR(new LocalPoint(7232, 7232)),
    WEST_PILLAR_NORTH(new LocalPoint(7232, 7104)),
    EAST_PILLAR(new LocalPoint(6208, 7232)),
    EAST_PILLAR_NORTH(new LocalPoint(6208, 7104)),
    SOUTH_EAST(new LocalPoint(6208, 8000)),
    SOUTH_EAST_MELEE(new LocalPoint(5952, 7744)),
    MIDDLE(new LocalPoint(6720, 6848));

    private LocalPoint position;

    public LocalPoint getPosition() {
        return this.position;
    }

    StandPosition(LocalPoint position) {
        this.position = position;
    }
}
