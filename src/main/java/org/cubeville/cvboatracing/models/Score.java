package org.cubeville.cvboatracing.models;

import org.cubeville.cvboatracing.TrackManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Score {

	private long finalTime;
	private UUID playerUUID;
	private Track track;
	private HashMap<Integer, Long> splits = new HashMap<>();

	public Score(long finalTime, Track track, UUID playerUUID) {
		this.finalTime = finalTime;
		this.track = track;
		this.playerUUID = playerUUID;
	}

	public void addSplit(int checkpoint, long value) {
		splits.put(checkpoint, value);
	}

	public long getSplit(int checkpoint) {
		return splits.get(checkpoint);
	}

	public long getFinalTime() {
		return finalTime;
	}

	public void setFinalTime(long finalTime) {
		this.finalTime = finalTime;
	}

	public void setSplits(HashMap<Integer, Long> splits) {
		this.splits = splits;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public Track getTrack() {
		return track;
	}

	public HashMap<Integer, Long> getSplits() {
		return splits;
	}
}
