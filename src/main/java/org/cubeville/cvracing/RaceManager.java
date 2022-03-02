package org.cubeville.cvracing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvracing.models.*;

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

	public static final List<EntityType> racingVehicles = Arrays.asList(EntityType.BOAT, EntityType.PIG, EntityType.HORSE);


	private static HashMap<UUID, Race> races = new HashMap<>();
	private static Set<Player> racingPlayers = new HashSet<>();
	private static HashMap<String, Integer> queueCountdowns = new HashMap<>();
	private static JavaPlugin plugin;

	public static void setPlugin(JavaPlugin javaPlugin) {
		plugin = javaPlugin;
	}

	public static void addTrialsRace(Track t, Player p) {
		Race race = new TrialsRace(t, plugin, p);
		races.put(p.getUniqueId(), race);
		racingPlayers.add(p);
	}

	public static void addVersusRace(Track t, Player p) {
		System.out.println(t.getVersusSpawns().size());
		if (t.getVersusSpawns().size() == 0) {
			p.sendMessage("§cThis track is not set up for versus mode. Please contact a server administrator.");
			return;
		}
		VersusRace race = new VersusRace(t, plugin);
		race.addPlayer(p);
		t.setVersusRace(race);
		races.put(p.getUniqueId(), race);
		racingPlayers.add(p);
		t.setStatus(TrackStatus.IN_LOBBY);
		t.getSigns().forEach(RaceSign::displayQueue);
	}

	public static void addPlayerToVersus(Track t, Player p) {
		VersusRace vr = t.getVersusRace();
		if (vr.playerSize() >= vr.maxPlayers) {
			p.sendMessage("§cThis race is full!");
			return;
		}
		if (vr.hasPlayer(p)) {
			p.sendMessage("§cYou are already in this race.");
			return;
		}
		vr.addPlayer(p);
		t.setVersusRace(vr);
		races.put(p.getUniqueId(), vr);
		racingPlayers.add(p);
		t.getSigns().forEach(RaceSign::displayQueue);
	}

	public static void removePlayerFromVersus(Track t, Player p) {
		VersusRace race = t.getVersusRace();
		if (race == null || !race.hasPlayer(p)) { return; }
		race.removePlayer(p);
		races.remove(p.getUniqueId());
		racingPlayers.remove(p);
		if (race.playerSize() == 0) {
			RaceManager.finishRace(t);
			t.setVersusRace(null);
		} else {
			t.setVersusRace(race);
		}
	}


	public static Race getRace(Player p) { return races.get(p.getUniqueId()); }

	public static void cancelRace(Player p, String subtitle) {
		Race race = getRace(p);
		if (race != null) {
			race.cancelRace(p, subtitle);
		}
	}

	public static void removeRace(Player p) {
		races.remove(p.getUniqueId());
		racingPlayers.remove(p);
	}

	public static void finishRace(Track t) {
		if (t.getQueue().size() > 0) {
			t.setStatus(TrackStatus.IN_USE);
			startRaceWithQueue(t);
		} else {
			t.setStatus(TrackStatus.OPEN);
			for (RaceSign sign : t.getSigns()) {
				sign.displayQueue();
			}
		}
	}

	public static Set<Player> getRacingPlayers() {
		return racingPlayers;
	}

	private static void startRaceWithQueue(Track t) {
		Player p = t.getPlayerFromQueue();
		TrackManager.clearPlayerFromQueues(p);
		if (p != null) {
			for (RaceSign sign : t.getSigns()) {
				sign.displayQueue();
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
					p.sendMessage("§eStarting trials race on the track §6§l" + t.getName() + "§e in " + counter + " seconds...");
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
			finishRace(t);
		} else {
			addTrialsRace(t, p);
		}
	}
}
