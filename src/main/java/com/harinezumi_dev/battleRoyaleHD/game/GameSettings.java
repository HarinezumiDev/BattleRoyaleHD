package com.harinezumi_dev.battleRoyaleHD.game;

import org.bukkit.Location;
import org.bukkit.boss.BarColor;

public class GameSettings {
    private int miningBorderDiameter;
    private int miningPhaseTime;
    private int fightBorderDiameter;
    private int fightPhaseTime;
    private int overtimeBorderDiameter;
    private int overtimePhaseTime;
    private Location spawnLocation;
    private boolean bossbarEnable;
    private BarColor phaseColorMining;
    private BarColor phaseColorFight;
    private BarColor phaseColorOvertime;
    private int invisibilityTime;
    private QuickDeathType quickDeathType;

    public GameSettings(int miningBorderDiameter, int miningPhaseTime,
                        int fightBorderDiameter, int fightPhaseTime,
                        int overtimeBorderDiameter, int overtimePhaseTime,
                        Location spawnLocation, boolean bossbarEnable,
                        BarColor phaseColorMining, BarColor phaseColorFight,
                        BarColor phaseColorOvertime, int invisibilityTime,
                        QuickDeathType quickDeathType) {
        this.miningBorderDiameter = miningBorderDiameter;
        this.miningPhaseTime = miningPhaseTime;
        this.fightBorderDiameter = fightBorderDiameter;
        this.fightPhaseTime = fightPhaseTime;
        this.overtimeBorderDiameter = overtimeBorderDiameter;
        this.overtimePhaseTime = overtimePhaseTime;
        this.spawnLocation = spawnLocation;
        this.bossbarEnable = bossbarEnable;
        this.phaseColorMining = phaseColorMining;
        this.phaseColorFight = phaseColorFight;
        this.phaseColorOvertime = phaseColorOvertime;
        this.invisibilityTime = invisibilityTime;
        this.quickDeathType = quickDeathType;
    }

    public int getMiningBorderDiameter() {
        return miningBorderDiameter;
    }

    public int getMiningPhaseTime() {
        return miningPhaseTime;
    }

    public int getFightBorderDiameter() {
        return fightBorderDiameter;
    }

    public int getFightPhaseTime() {
        return fightPhaseTime;
    }

    public int getOvertimeBorderDiameter() {
        return overtimeBorderDiameter;
    }

    public int getOvertimePhaseTime() {
        return overtimePhaseTime;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public boolean isBossbarEnable() {
        return bossbarEnable;
    }

    public BarColor getPhaseColorMining() {
        return phaseColorMining;
    }

    public BarColor getPhaseColorFight() {
        return phaseColorFight;
    }

    public BarColor getPhaseColorOvertime() {
        return phaseColorOvertime;
    }

    public int getInvisibilityTime() {
        return invisibilityTime;
    }

    public QuickDeathType getQuickDeathType() {
        return quickDeathType;
    }
}
