package org.cubeville.cvboatracing.models;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.cubeville.cvboatracing.*;

import java.util.HashMap;
import java.util.UUID;

public class Race {
	private JavaPlugin plugin;
	private Track track;
	private Player player;
	private Score comparingTime;
	private Score personalBest;
	private HashMap<Integer, Long> splits = new HashMap<>();
	private int checkpointIndex;
	private int countdownTimer;
	private int countdownFreeze;
	private int stopwatch;
	private int minuteCap;
	private long startTime;
	private long endTime;

	public Race(Track track, Player player, JavaPlugin plugin) {
		this.track = track;
		this.player = player;
		this.checkpointIndex = 0;
		this.plugin = plugin;
		this.checkpointIndex = 0;
		this.countdownTimer = 0;
		this.minuteCap = 3; // The player can go for x minutes before they are kicked out of the game
		this.personalBest = ScoreManager.getScore(player.getUniqueId(), track);
		this.comparingTime = determineComparingTime();

		this.startRace();
	}

	private Score determineComparingTime() {
		if (SelectedSplits.isUsingWR(player.getUniqueId())) {
			return ScoreManager.getWRScore(track);
		}

		UUID selectedSplitPlayer = SelectedSplits.getSelectedSplitPlayer(player.getUniqueId());
		if (selectedSplitPlayer != null) {
			return ScoreManager.getScore(selectedSplitPlayer, track);
		}
		return this.personalBest;
	}

	public void startRace() {
		this.track.setStatus(TrackStatus.IN_USE);
		TrackManager.clearPlayerFromQueues(player);
		if (!this.track.getSpawn().getChunk().isLoaded()) {
			this.track.getSpawn().getChunk().load();
		}
		player.teleport(this.track.getSpawn());
		Boat b = (Boat)this.player.getWorld().spawnEntity(this.track.getSpawn(), EntityType.BOAT);
		b.addPassenger(this.player);
		b.setVelocity(new Vector(0, 0, 0));
		runCountdown(3);
	}

	public Location getCurrentCheckpoint() {
		return this.track.getCheckpoints().get(this.checkpointIndex);
	}

	public void advanceCheckpoint() {
		if (((TripwireHook) this.getCurrentCheckpoint().getBlock().getBlockData()).isPowered()) {
			if (checkpointIndex == this.track.getCheckpoints().size() - 1) {
				stopStopwatch();
				this.completeRace();
			} else if (checkpointIndex != 0) {
				long currentTime = System.currentTimeMillis() - startTime;
				splits.put(checkpointIndex, currentTime);
				player.sendMessage("§6CP" + checkpointIndex + ": " + BoatRaceUtilities.formatTimeString(currentTime) + getSplitString(currentTime));
			}
			checkpointIndex++;
		}
	}

	private String getSplitString(long currentTime) {
		if (comparingTime != null) {
			long comparingSplit = comparingTime.getSplit(checkpointIndex);
			String comparingName = "";
			if (!comparingTime.getPlayerUUID().equals(player.getUniqueId())) {
				comparingName = " -- " + comparingTime.getPlayerName();
			}
			if (comparingSplit > currentTime) {
				return " §6(§a-" + BoatRaceUtilities.formatTimeString(comparingSplit - currentTime) + "§6)" + comparingName;
			} else if (comparingSplit < currentTime) {
				return " §6(§c+" + BoatRaceUtilities.formatTimeString(currentTime - comparingSplit) + "§6)" + comparingName;
			} else {
				return " §6(§e00:00.000§6)" + comparingName;
			}
		}
		return "";
	}

	public void cancelRace(String subtitle) {
		player.sendTitle( ChatColor.RED + "Race ended", ChatColor.DARK_RED + subtitle, 5, 90, 5);
		if (this.countdownTimer != 0) { endCountdown(); }
		if (this.stopwatch != 0) { stopStopwatch(); }
		this.finishRace();
	}

	public void completeRace() {
		long finalTime = this.endTime - this.startTime;
		String pbString = " ";
		if (personalBest == null || personalBest.getFinalTime() > finalTime) {
			String pbBy = "";
			if (personalBest != null) {
				pbBy = ", which was your personal best by " + BoatRaceUtilities.formatTimeString(personalBest.getFinalTime() - finalTime);
			}
			player.sendMessage("§b You achieved a time of " + BoatRaceUtilities.formatTimeString(finalTime) + " on " + track.getName() + pbBy + "!");
			pbString = "§a§l New Personal Best!";
			Score wr = ScoreManager.getWRScore(track);
			ScoreManager.setNewPB(player.getUniqueId(), track, finalTime, splits);
			if (wr == null || finalTime < wr.getFinalTime()) {
				Bukkit.getServer().broadcastMessage("§b§l" + player.getName() + "§3 just got a new world record time of §b§l" + BoatRaceUtilities.formatTimeString(finalTime) + "§3 on §b§l" + track.getName() + "!");
			} else {
				Bukkit.getServer().broadcastMessage("§d§l" + player.getName() + "§5 just got a new personal best time of §d§l" + BoatRaceUtilities.formatTimeString(finalTime) + "§5, which put them at rank §d§l#" + ScoreManager.getScorePlacement(track, player.getUniqueId()) + "§5 on §d§l" + track.getName() + "!");
			}
			if (ScoreManager.shouldRefreshLeaderboard(finalTime, track)) {
				track.loadLeaderboards();
			}
		} else {
			player.sendMessage("§b You achieved a time of " + BoatRaceUtilities.formatTimeString(finalTime) + " on " + track.getName() + ", which was " + BoatRaceUtilities.formatTimeString(finalTime - personalBest.getFinalTime()) + " behind your personal best!");
		}
		player.sendTitle("§d§l" + BoatRaceUtilities.formatTimeString(this.endTime - this.startTime), pbString, 5, 90, 5);
		this.finishRace();
	}

	public void finishRace() {
		if (track.getQueue().size() == 0) {
			track.setStatus(TrackStatus.OPEN);
		}
		RaceManager.removeRace(player, track);
		Entity v = player.getVehicle();
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			player.teleport(track.getExit());
			if (v != null) { v.remove(); }
		}, 1L);

	}

	private void runCountdown(int startCount) {
		this.countdownTimer = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
			int counter = startCount;
			@Override
			public void run() {
				if (counter > 0) {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3.0F, 0.7F);
					player.sendTitle(ChatColor.RED + String.valueOf(counter), null, 1, 18, 1);
					counter--;
				} else {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 3.0F, 1.4F);
					player.sendTitle(ChatColor.GREEN + "Go!", null, 1, 38, 1);
					endCountdown();
				}
			}
		}, 0L, 20L);
		this.countdownFreeze = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			Vehicle v = (Vehicle) player.getVehicle();
			@Override
			public void run() {
				if (v != null) {
					v.setVelocity(new Vector(0, 0, 0));
					v.teleport(track.getSpawn());
				}
			}
		}, 0L, 1L);
	}

	private void endCountdown() {
		Bukkit.getScheduler().cancelTask(this.countdownTimer);
		Bukkit.getScheduler().cancelTask(this.countdownFreeze);
		this.startTime = System.currentTimeMillis();
		countdownTimer = 0;
		countdownFreeze = 0;
		startStopwatch();
	}

	private void startStopwatch() {
		this.stopwatch = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
			long elapsed = System.currentTimeMillis() - startTime;
			if ((int) elapsed / 60000 >= minuteCap) { cancelRace("You took too long to finish.");}
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§a§l" + BoatRaceUtilities.formatTimeString(elapsed)));
		}, 0L, 1L);
	}

	private void stopStopwatch() {
		endTime = System.currentTimeMillis();
		Bukkit.getScheduler().cancelTask(this.stopwatch);
		stopwatch = 0;
	}

	public Track getTrack() {
		return track;
	}
}
