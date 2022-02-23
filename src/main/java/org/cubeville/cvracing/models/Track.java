package org.cubeville.cvracing.models;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.cubeville.cvracing.RaceUtilities;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackStatus;
import org.cubeville.cvracing.TrackType;

import java.util.*;

public class Track implements Listener {
	private String name;
	private String displayName;
	private TrackStatus status;
	private TrackType type = TrackType.BOAT;
	private Location spawn;
	private Location exit;
	private List<RaceSign> signs = new ArrayList<>();
	private List<Location> checkpoints = new ArrayList<>();
	private List<Leaderboard> leaderboards = new ArrayList<>();
	private Queue<Player> queue = new LinkedList<>();

	public Track(String name) {
		this.name = name;
		this.status = TrackStatus.OPEN;
	}

	public Location getExit() {
		return this.exit;
	}

	public void setExit(Location exit) {
		this.exit = exit;
	}

	public Location getSpawn() {
		return this.spawn;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	public String getName() {
		return name;
	}

	public TrackStatus getStatus() {
		return status;
	}

	public void setStatus(TrackStatus status) {
		this.status = status;
		for (RaceSign sign : this.signs) {
			sign.displayStatus(status);
		}
	}

	public void addSign(RaceSign raceSign) {
		this.signs.add(raceSign);
	}
	public void addCheckpoint(Location checkpoint) { this.checkpoints.add(checkpoint); }
	public void removeCheckpoint(int index) { this.checkpoints.remove(index); }
	public void removeSign(int index) { this.signs.remove(index); }

	public void addLeaderboard(Location lbLoc) {
		leaderboards.add(new Leaderboard(lbLoc));
	}
	public void removeLeaderboard(int index) {
		this.leaderboards.get(index).clear();
		this.leaderboards.remove(index);
	}

	public void loadLeaderboards() {
		leaderboards.forEach(leaderboard -> leaderboard.setDisplay(RaceUtilities.getLeaderboardLines(this, 0, 9)));
	}

	public List<Leaderboard> getLeaderboards() {
		return leaderboards;
	}

	public void clearLeaderboards() {
		leaderboards.forEach(Leaderboard::clear);
	}

	public List<Location> getCheckpoints() {
		return checkpoints;
	}

	public void onRightClick(Player p) {
		switch (status) {
			case OPEN:
				RaceManager.addRace(this, p);
				break;
			case IN_USE:
				this.addToQueue(p);
				break;
			case CLOSED:
				p.sendMessage(ChatColor.RED + "This track is currently closed. Please try again later.");
		}
	}

	private void addToQueue(Player p) {
		if (queue.contains(p)) {
			p.sendMessage(ChatColor.RED + "You are already in the queue!");
		} else {
			queue.add(p);
			p.sendMessage(ChatColor.YELLOW + "You have been been added to the queue!");
			for (RaceSign sign : this.signs) {
				sign.displayQueue(queue.size());
			}
		}
	}

	public Queue<Player> getQueue() {
		return queue;
	}

	public Player getPlayerFromQueue() {
		return queue.poll();
	}

	public List<RaceSign> getSigns() {
		return signs;
	}

	public void removePlayerFromQueue(Player p) {
		if (queue.contains(p)) {
			queue.remove(p);
		}
	}

	public TrackType getType() {
		return type;
	}

	public void setType(TrackType type) {
		this.type = type;
	}
}
