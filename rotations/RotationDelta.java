package net.runelite.client.plugins.zulrah.rotations;

import net.runelite.client.plugins.zulrah.enums.Equipment;
import net.runelite.client.plugins.zulrah.enums.Prayer;
import net.runelite.client.plugins.zulrah.enums.StandPosition;

import java.util.ArrayList;

public class RotationDelta extends Rotation{

    public RotationDelta() {
        System.out.println("@DELTA");
        this.totalPhases = 12;
    }

    @Override
    public StandPosition getStandPosition() {
        switch(this.currentPhase) {
            case 1:
            case 12:
                return StandPosition.SOUTH_WEST_CORNER;
            case 2:
                return StandPosition.SOUTH_WEST_CORNER_MELEE;
            case 3:
                return StandPosition.EAST_PILLAR;
            case 4:
            case 8:
                return StandPosition.SOUTH_EAST_MELEE;
            case 5:
            case 6:
            case 9:
            case 10:
            case 11:
                return StandPosition.WEST_PILLAR;
            case 7:
                if (currentAnimationCount < 5) {
                    return StandPosition.WEST_PILLAR;
                }
                return StandPosition.EAST_PILLAR_NORTH;
        }

        return null;
    }

    @Override
    public Equipment getEquipment() {
        switch(this.currentPhase) {
            case 1:
            case 2:
            case 4:
            case 8:
            case 12:
                return Equipment.RANGE;
            case 3:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
                return Equipment.MAGE;
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
            case 2:
            case 4:
            case 8:
            case 10:
                prayers.add(Prayer.PROTECTED_FROM_MAGIC);
                break;
            case 3:
            case 6:
            case 9:
                prayers.add(Prayer.PROTECTED_FROM_RANGED);
                break;
            case 1:
            case 5:
            case 7:
            case 12:
                // no prayers
                break;
            case 11:
                // jad
                if (currentAnimationCount % 2 == 0) {
                    prayers.add(Prayer.PROTECTED_FROM_MAGIC);
                } else {
                    prayers.add(Prayer.PROTECTED_FROM_RANGED);
                }
                break;
        }

        return prayers;
    }
}
