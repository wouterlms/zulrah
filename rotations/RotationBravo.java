package net.runelite.client.plugins.zulrah.rotations;

import net.runelite.client.plugins.zulrah.enums.Equipment;
import net.runelite.client.plugins.zulrah.enums.Prayer;
import net.runelite.client.plugins.zulrah.enums.StandPosition;

import java.util.ArrayList;

public class RotationBravo extends Rotation {

    public RotationBravo() {
        System.out.println("@BRAVO");
        this.totalPhases = 10;
    }

    @Override
    public StandPosition getStandPosition() {
        switch (this.currentPhase) {
            case 1:
            case 3:
                return StandPosition.SOUTH_WEST_CORNER;
            case 2:
            case 10:
                if (ticks < 10) {
                    return StandPosition.SOUTH_WEST_CORNER;
                }
                return StandPosition.SOUTH_WEST_CORNER_MELEE;
            case 4:
            case 8:
                return StandPosition.EAST_PILLAR_NORTH;
            case 5:
                if (currentAnimationCount < 8) {
                    return StandPosition.EAST_PILLAR_NORTH;
                }
                this.dontAttack = true;
                return StandPosition.EAST_PILLAR;
            case 6:
                return StandPosition.EAST_PILLAR;
            case 7:
                return StandPosition.WEST_PILLAR_NORTH;
            case 9:
                if (currentAnimationCount < 10) {
                    return StandPosition.SOUTH_EAST;
                }
                this.dontAttack = true;
                return StandPosition.SOUTH_WEST_CORNER;
        }
        return null;
    }

    @Override
    public Equipment getEquipment() {
        switch (this.currentPhase) {
            case 1:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 10:
                return Equipment.MAGE;
            case 3:
            case 5:
            case 8:
                return Equipment.RANGE;
        }

        return null;
    }

    @Override
    public ArrayList<Prayer> getPrayers() {
        ArrayList<Prayer> prayers = new ArrayList<>();

        if (!this.dontAttack) {
            if (getEquipment().equals(Equipment.RANGE)) {
                prayers.add(Prayer.EAGLE_EYE);
            } else {
                prayers.add(Prayer.MYSTIC_MIGHT);
            }
        }

        switch (this.currentPhase) {
            case 3:
            case 8:
                prayers.add(Prayer.PROTECTED_FROM_MAGIC);
                break;
            case 5:
                if (currentAnimationCount < 8) {
                    prayers.add(Prayer.PROTECTED_FROM_MAGIC);
                }
                break;
            case 7:
                prayers.add(Prayer.PROTECTED_FROM_RANGED);
                break;
            case 4:
            case 6:
            case 10:
                // no prayer
                break;
            case 9:
                // jad
                if (currentAnimationCount < 10) {
                    if (currentAnimationCount % 2 == 0) {
                        prayers.add(Prayer.PROTECTED_FROM_RANGED);
                    } else {
                        prayers.add(Prayer.PROTECTED_FROM_MAGIC);
                    }
                }
                break;
        }

        return prayers;
    }
}
