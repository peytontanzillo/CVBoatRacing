package org.cubeville.cvboatracing.models;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.cubeville.cvboatracing.RaceManager;
import org.cubeville.cvboatracing.TrackStatus;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Track implements Listener {
	private String name;
	private TrackStatus status;
	private Location spawn;
	private Location exit;
	private List<RaceSign> signs = new ArrayList<>();
	private List<Location> checkpoints = new ArrayList<>();
	private Queue<Player> queue = new LinkedList<>();
	private List<Score> scores = new ArrayList<>();

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

	public void addScore(Score score) {
		this.scores.add(score);
	}
}
