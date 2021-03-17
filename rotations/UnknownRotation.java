package net.runelite.client.plugins.zulrah.rotations;

import net.runelite.client.plugins.zulrah.enums.Equipment;
import net.runelite.client.plugins.zulrah.enums.Prayer;
import net.runelite.client.plugins.zulrah.enums.StandPosition;

import java.util.ArrayList;

public class UnknownRotation extends Rotation {
    public UnknownRotation() {
        super();
    }

    @Override
    public StandPosition getStandPosition() {
        switch(this.currentPhase) {
            case 1:
            case 3:
                return StandPosition.SOUTH_WEST_CORNER;
            case 2:
                if (ticks < 10) {
                    return StandPosition.SOUTH_WEST_CORNER;
                }
                return StandPosition.SOUTH_WEST_CORNER_MELEE;
            case 4:
                return StandPosition.EAST_PILLAR_NORTH;
        }

        return null;
    }

    @Override
    public Equipment getEquipment() {
        switch (this.currentPhase) {
            case 1:
            case 2:
            case 4:
                return Equipment.MAGE;
            case 3:
                return Equipment.RANGE;
        }
        return null;
    }

    @Override
    public ArrayList<Prayer> getPrayers() {
        ArrayList<Prayer> prayers = new ArrayList<>();

        if (getEquipment().equals(Equipment.RANGE)) {
            prayers.add(Prayer.EAGLE_EYE);
        } else {
            prayers.add(Prayer.MYSTIC_MIGHT);
        }

        switch (this.currentPhase) {
            case 1:
            case 2:
            case 4:
                break;
            case 3:
                prayers.add(Prayer.PROTECTED_FROM_MAGIC);
                break;
        }

        return prayers;
    }
}
