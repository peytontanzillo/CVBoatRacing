package org.cubeville.cvboatracing.models;

import org.bukkit.Bukkit;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

public class Score {

	private long finalTime;
	private UUID playerUUID;
	private String playerName;
	private long timestamp;
	private Track track;
	private HashMap<Integer, Long> splits = new HashMap<>();

	public Score(long finalTime, Track track, UUID playerUUID, long timestamp) {
		this.finalTime = finalTime;
		this.timestamp = timestamp;
		this.track = track;
		this.playerUUID = playerUUID;
		this.playerName = Bukkit.getOfflinePlayer(playerUUID).getName();
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

	public void setSplits(HashMap<Integer, Long> splits) {
		this.splits = splits;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Track getTrack() {
		return track;
	}

	public HashMap<Integer, Long> getSplits() {
		return splits;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
