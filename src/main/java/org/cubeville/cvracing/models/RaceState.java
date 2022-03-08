package org.cubeville.cvracing.models;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RaceState {

    private HashMap<Integer, Long> splits = new HashMap<>();
    private Player player;
    private int checkpointIndex = 0;
    private int lapIndex = 0;
    private int countdown = 0;
    private int stopwatch = 0;
    private long elapsed = 0;
    private long endTime = 0;
    private boolean isCanceled = false;
    private Location resetLocation;
    private ArmorStand armorStand;

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

    public void addSplit(int index, long time) {
        System.out.println("Adding split at " + index);
        this.splits.put(index, time);
    }

    public void setCheckpointIndex(int index) {
        this.checkpointIndex = index;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setStopwatch(int stopwatch) { this.stopwatch = stopwatch; }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime() {
        this.endTime = elapsed;
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
}
