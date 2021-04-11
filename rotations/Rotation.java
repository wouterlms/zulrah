package net.runelite.client.plugins.zulrah.rotations;

import net.runelite.client.plugins.zulrah.enums.Equipment;
import net.runelite.client.plugins.zulrah.enums.Prayer;
import net.runelite.client.plugins.zulrah.enums.StandPosition;

import java.util.ArrayList;

public abstract class Rotation {

    protected int currentPhase = 1;
    protected int totalPhases = 0;
    protected int currentAnimationCount;
    protected int ticks = 0;
    protected boolean hasCompletedRotation = false;
    protected boolean isZulrahReady = true;
    protected boolean dontAttack = false;


    // currentAnimationCount is not being used yet, but could be useful in the future
    public void increaseAnimationCount() {
        currentAnimationCount++;
    }

    public void increaseTicks() {
        ticks++;
    }

    // set next phase
    public void nextPhase() {
        this.dontAttack = false;
        this.currentAnimationCount = 0;

        if (currentPhase == totalPhases) {
            System.out.println("@reset");
            this.currentPhase = 1;
            this.hasCompletedRotation = true;
        } else {
            System.out.println("@nextPhase");
            this.currentPhase++;
        }
    }

    public abstract StandPosition getStandPosition();

    public abstract Equipment getEquipment();

    public abstract ArrayList<Prayer> getPrayers();

    public int getCurrentPhase() {
        return currentPhase;
    }

    public boolean isZulrahReady() {
        return isZulrahReady;
    }

    public void setIsZulrahReady(boolean zulrahReady) {
        isZulrahReady = zulrahReady;
        ticks = 0;
    }

    public void setCurrentPhase(int phase) {
        this.currentPhase = phase;
    }

    public boolean getDontAttack() {
        return dontAttack;
    }
}
