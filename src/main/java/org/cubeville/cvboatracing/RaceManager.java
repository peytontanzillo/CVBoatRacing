package org.cubeville.cvboatracing;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvboatracing.models.Race;
import org.cubeville.cvboatracing.models.RaceSign;
import org.cubeville.cvboatracing.models.Track;

import java.util.HashMap;
import java.util.UUID;

public class RaceManager {

	private static HashMap<UUID, Race> races = new HashMap<>();
	private static HashMap<String, Integer> queueCountdowns = new HashMap<>();
	private static JavaPlugin plugin;

	public static void setPlugin(JavaPlugin javaPlugin) {
		plugin = javaPlugin;
	}

	public static void addRace(Track t, Player p) {
		Race race = new Race(t, p, plugin);
		races.put(p.getUniqueId(), race);
	}

	public static Race getRace(Player p) {
		return races.get(p.getUniqueId());
	}

	public static void advanceCheckpoints(Player p) {
		Race race  = getRace(p);
		if (race != null) {
			race.advanceCheckpoint();
		}
	}

	public static void cancelRace(Player p, String subtitle) {
		Race race  = getRace(p);
		if (race != null) {
			race.cancelRace(subtitle);
		}
	}

	public static void removeRace(Player p1, Track t) {
		races.remove(p1.getUniqueId());
		if (t.getQueue().size() > 0) {
			startRaceWithQueue(t);
		}
	}

	private static void startRaceWithQueue(Track t) {
		Player p = t.getPlayerFromQueue();
		TrackManager.clearPlayerFromQueues(p);
		if (p != null) {
			int queueSize = t.getQueue().size();
			for (RaceSign sign : t.getSigns()) {
				sign.displayQueue(queueSize);
			}
			warnJoiningPlayer(p, t);
		}
	}

	private static void warnJoiningPlayer(Player p, Track t) {
		queueCountdowns.put(t.getName(), Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int counter = 3;
			@Override
			public void run() {
				if (counter > 0 && Bukkit.getServer().getPlayerExact(p.getName()) != null) {
					p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 3.0F, 0.7F);
					p.sendMessage("§eStarting race on the track §6§l" + t.getName() + "§e in " + counter + " seconds...");
					counter--;
				} else {
					startRaceOfQueuedPlayer(p, t);
				}
			}
		}, 0L, 20L));
	}

	private static void startRaceOfQueuedPlayer(Player p, Track t) {
		Bukkit.getScheduler().cancelTask(queueCountdowns.get(t.getName()));
		queueCountdowns.put(t.getName(), 0);
		if (Bukkit.getServer().getPlayerExact(p.getName()) == null) {
			// they left during the countdown
			if (t.getQueue().size() > 0) {
				startRaceWithQueue(t);
			} else {
				t.setStatus(TrackStatus.OPEN);
			}
		} else {
			addRace(t, p);
		}
	}

}
