package org.cubeville.cvracing.models;

import java.util.HashMap;

public class RaceState {
    private HashMap<Integer, Long> splits = new HashMap<>();
    private int checkpointIndex = 0;
    private int countdown = 0;
    private int stopwatch = 0;
    private int placement = 0;
    private long elapsed = 0;
    private long endTime = 0;

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

    public int getPlacement() { return placement; }

    public void addSplit(int index, long time) {
        this.splits.put(index, time);
    }

    public void setCheckpointIndex(int index) {
        this.checkpointIndex = index;
    }

    public void setPlacement(int placement) { this.placement = placement; }

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
}
