package org.cubeville.cvracing.models;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.cubeville.cvracing.RaceManager;

import java.util.HashMap;

public class RaceState {

    private HashMap<Integer, Long> splits = new HashMap<>();
    private Player player;
    private Location previousTickLocation;
    private long previousTick;
    private int checkpointIndex = 0;
    private int lapIndex = 0;
    private int countdown = 0;
    private int stopwatch = 0;
    private long elapsed = 0;
    private long finishTime = 0;
    private long startTime = 0;
    private boolean isCanceled = false;
    private Location resetLocation;
    private ArmorStand armorStand;
    private boolean isSpectator = false;

    public RaceState(Player player) {
        this.player = player;
    }

    public HashMap<Integer, Long> getSplits() {
        return splits;
    }

    public Long getSplit(int index) {
        return splits.get(index);
    }

    public int getCheckpointIndex() {
        return checkpointIndex;
    }

    public int getCountdown() { return countdown; }

    public int getStopwatch() { return stopwatch; }

    public void addSplit(int index, long time) { this.splits.put(index, time); }

    public void setCheckpointIndex(int index) {
        this.checkpointIndex = index;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setStopwatch(int stopwatch) { this.stopwatch = stopwatch; }

    public long getElapsed() {
        if (RaceManager.getTiming().equals("TPS")) {
            return elapsed;
        } else {
            return System.currentTimeMillis() - startTime;
        }
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public ArmorStand getArmorStand() {
        return armorStand;
    }

    public void setArmorStand(ArmorStand armorStand) {
        this.armorStand = armorStand;
    }

    public Player getPlayer() {
        return player;
    }

    public int getLapIndex() {
        return lapIndex;
    }

    public void setLapIndex(int lapIndex) {
        this.lapIndex = lapIndex;
    }

    public Location getResetLocation() {
        return resetLocation;
    }

    public void setResetLocation(Location resetLocation) {
        this.resetLocation = resetLocation;
    }

    public boolean isSpectator() {
        return isSpectator;
    }

    public void setSpectator(boolean spectator) {
        isSpectator = spectator;
    }

    public void reset() {
        this.checkpointIndex = 0;
        this.lapIndex = 0;
        this.finishTime = 0;
        this.startTime = 0;
        this.elapsed = 0;
        this.splits.clear();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Location getPreviousTickLocation() {
        return previousTickLocation;
    }

    public void setPreviousTickLocation(Location previousTickLocation) {
        this.previousTickLocation = previousTickLocation;
    }

    public long getPreviousTick() {
        return previousTick;
    }

    public void setPreviousTick(long previousTick) {
        this.previousTick = previousTick;
    }
}
