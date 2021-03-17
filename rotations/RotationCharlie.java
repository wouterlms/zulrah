package net.runelite.client.plugins.zulrah.rotations;

import net.runelite.client.plugins.zulrah.enums.Equipment;
import net.runelite.client.plugins.zulrah.enums.Prayer;
import net.runelite.client.plugins.zulrah.enums.StandPosition;

import java.util.ArrayList;

public class RotationCharlie extends Rotation{
    public RotationCharlie() {
        System.out.println("@CHARLIE");
        this.totalPhases = 11;
    }

    @Override
    public StandPosition getStandPosition() {
        switch(this.currentPhase) {
            case 1:
            case 2:
            case 11:
                return StandPosition.SOUTH_WEST_CORNER;
            case 3:
                if (ticks < 25 || ticks > 32) {
                    return StandPosition.SOUTH_EAST_MELEE;
                }
                return StandPosition.SOUTH_EAST;
            case 4:
                return StandPosition.SOUTH_EAST;
            case 5:
            case 6:
            case 10:
                return StandPosition.WEST_PILLAR;
            case 7:
            case 8:
                return StandPosition.SOUTH_EAST_MELEE;
            case 9:
                if (currentAnimationCount < 5) {
                    return StandPosition.SOUTH_EAST_MELEE;
                }
                return StandPosition.WEST_PILLAR_NORTH;
        }

        return null;
    }

    @Override
    public Equipment getEquipment() {
        switch(this.currentPhase) {
            case 1:
            case 2:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
                return Equipment.MAGE;
            case 4:
            case 6:
            case 9:
            case 11:
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
            case 2:
            case 5:
            case 8:
                prayers.add(Prayer.PROTECTED_FROM_RANGED);
                break;
            case 1:
            case 3:
            case 11:
                // none
                break;
            case 4:
            case 6:
            case 9:
                prayers.add(Prayer.PROTECTED_FROM_MAGIC);
                break;
            case 10:
                System.out.println("% 2 " + currentAnimationCount % 2 );
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
