package org.cubeville.cvracing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.RaceSign;
import org.cubeville.cvracing.models.Track;

import java.util.*;

public class RaceManager {

	public static final List<Material> checkpointTriggerTypes = Arrays.asList(
			Material.TRIPWIRE,
			Material.TRIPWIRE_HOOK,
			Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
			Material.ACACIA_PRESSURE_PLATE,
			Material.BIRCH_PRESSURE_PLATE,
			Material.CRIMSON_PRESSURE_PLATE,
			Material.DARK_OAK_PRESSURE_PLATE,
			Material.JUNGLE_PRESSURE_PLATE,
			Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
			Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
			Material.SPRUCE_PRESSURE_PLATE,
			Material.OAK_PRESSURE_PLATE,
			Material.STONE_PRESSURE_PLATE,
			Material.WARPED_PRESSURE_PLATE
	);

	public static final List<Material> checkpointItemTypes = checkpointTriggerTypes.subList(1, checkpointTriggerTypes.size() - 1);

	public static final List<EntityType> racingVehicles = Arrays.asList(EntityType.BOAT, EntityType.PIG, EntityType.HORSE);


	private static HashMap<UUID, Race> races = new HashMap<>();
	private static Set<Player> racingPlayers = new HashSet<>();
	private static HashMap<String, Integer> queueCountdowns = new HashMap<>();
	private static JavaPlugin plugin;

	public static void setPlugin(JavaPlugin javaPlugin) {
		plugin = javaPlugin;
	}

	public static void addRace(Track t, Player p) {
		Race race = new Race(t, p, plugin);
		races.put(p.getUniqueId(), race);
		racingPlayers.add(p);
	}

	public static Race getRace(Player p) {
		return races.get(p.getUniqueId());
	}

	public static void advanceCheckpoints(Player p) {
		Race race  = getRace(p);
		if (race != null) {
			race.advanceCheckpointIfShould();
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
		racingPlayers.remove(p1);
		if (t.getQueue().size() > 0) {
			startRaceWithQueue(t);
		}
	}

	public static Set<Player> getRacingPlayers() {
		return racingPlayers;
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
