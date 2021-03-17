package net.runelite.client.plugins.zulrah.helpers;

import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.zulrah.ZulrahPlugin;

public class LocalPlayer {
    private ZulrahPlugin plugin;

    public LocalPlayer(ZulrahPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isMoving() {
       //return plugin.getClient().getLocalDestinationLocation() != null;
        return plugin.getClient().getLocalPlayer().getIdlePoseAnimation() != plugin.getClient().getLocalPlayer().getPoseAnimation();
    }

    public boolean hasTarget() {
        return plugin.getClient().getLocalPlayer().getInteracting() != null;
    }

    public int getHealth() {
        return plugin.getClient().getBoostedSkillLevel(Skill.HITPOINTS);
    }

    public int distanceTo(LocalPoint point) {
        LocalPoint playerPoint = plugin.getClient().getLocalPlayer().getLocalLocation();
        return (int) Math.sqrt(Math.pow(playerPoint.getX() - point.getX(), 2) + Math.pow(playerPoint.getY() - playerPoint.getY(), 2));
    }

    public int getSpecialAttack() {
        return plugin.getClient().getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
    }

    public boolean isSpecialAttackEnabled() {
        return plugin.getClient().getVar(VarPlayer.SPECIAL_ATTACK_ENABLED) == 1;
    }
}

