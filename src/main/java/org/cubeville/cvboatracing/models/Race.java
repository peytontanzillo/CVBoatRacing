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
import org.cubeville.cvboatracing.RaceManager;
import org.cubeville.cvboatracing.TrackStatus;

public class Race {
	private JavaPlugin plugin;
	private Track track;
	private Player player;
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
		this.startRace();
	}

	public void startRace() {
		this.track.setStatus(TrackStatus.IN_USE);
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
			checkpointIndex++;
			if (checkpointIndex == this.track.getCheckpoints().size()) {
				stopStopwatch();
				this.completeRace();
			} else if (checkpointIndex != 1) {
				//this.player.sendTitle(" ", "§7§lCP" + checkpointIndex + ": " + formatTimeString(System.currentTimeMillis() - startTime), 5, 20,5);
				player.sendMessage("§aCP" + checkpointIndex + ": " + formatTimeString(System.currentTimeMillis() - startTime));
			}
		}
	}

	public void cancelRace(String subtitle) {
		player.sendTitle( ChatColor.RED + "Race ended", ChatColor.DARK_RED + subtitle, 5, 90, 5);
		if (this.countdownTimer != 0) { endCountdown(); }
		if (this.stopwatch != 0) { stopStopwatch(); }
		this.finishRace();
	}

	public void completeRace() {
		Bukkit.getServer().broadcastMessage("§d§l" + player.getName() + "§5 just got a time of §d§l" + formatTimeString(this.endTime - this.startTime) + "§5 on " + track.getName() + "!");
		player.sendTitle("§d§l" + formatTimeString(this.endTime - this.startTime), null, 5, 90, 5);
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
		this.stopwatch = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				long elapsed = System.currentTimeMillis() - startTime;
				if ((int) elapsed / 60000 >= minuteCap) { cancelRace("You took too long to finish.");}
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("§a§l" + formatTimeString(elapsed)));
			}
		}, 0L, 1L);
	}

	private void stopStopwatch() {
		endTime = System.currentTimeMillis();
		Bukkit.getScheduler().cancelTask(this.stopwatch);
		stopwatch = 0;
	}

	private String formatTimeString(long time) {
		return String.format("%d:%02d:%02d", (int) time / 60000, (int) (time / 1000) % 60, (int) (time / 10) % 100);
	}

	public Track getTrack() {
		return track;
	}
}
