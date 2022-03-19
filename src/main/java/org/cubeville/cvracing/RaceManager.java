package org.cubeville.cvracing;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvracing.models.*;

import java.util.*;

public class RaceManager {

	public static final List<EntityType> racingVehicles = Arrays.asList(EntityType.BOAT, EntityType.PIG, EntityType.HORSE, EntityType.STRIDER);


	private static HashMap<Player, Race> races = new HashMap<>();
	private static HashMap<String, Integer> queueCountdowns = new HashMap<>();
	private static JavaPlugin plugin;

	public static void setPlugin(JavaPlugin javaPlugin) {
		plugin = javaPlugin;
	}

	public static void addTrialsRace(Track t, Player p) {
		Race race = new TrialsRace(t, plugin, p);
		races.put(p, race);
	}

	public static void addVersusRace(Track t, Player p, int laps) {
		if (t.getVersusSpawns().size() == 0) {
			p.sendMessage("§cThis track is not set up for versus mode. Please contact a server administrator.");
			return;
		}
		VersusRace race = new VersusRace(t, plugin, laps);
		race.addPlayer(p);
		t.setVersusRace(race);
		races.put(p, race);
		t.setStatus(TrackStatus.IN_LOBBY);
		t.getSigns().forEach(RaceSign::displayQueue);
	}

	public static void addHostedRace(Track t, Player p) {
		RaceManager.cancelTrackRaces(t, "This track will be used for hosting");
		HostedRace race = new HostedRace(t, plugin, p);
		t.setHostedRace(race);
		races.put(p, race);
		p.teleport(t.getSpectate());
		for (RaceSign sign : t.getSigns()) {
			sign.displayType();
			sign.displayStatus(TrackStatus.HOSTING);
			sign.displayQueue();
		}
	}

	public static void addPlayerToHostedLobby(Track t, Player p) {
		HostedRace hr = t.getHostedRace();
		hr.addPlayerToLobby(p);
		hr.getHostingPlayer().sendMessage(ChatColor.YELLOW + p.getDisplayName() + " has joined the hosted lobby");
		p.sendMessage(ChatColor.YELLOW + "You have joined the hosted lobby");
		p.teleport(t.getSpectate());
		races.put(p, hr);
	}

	public static void removePlayerFromHostedLobby(Track t, Player p) {
		HostedRace hr = t.getHostedRace();
		if (hr == null) { return; }
		hr.removePlayerFromLobby(p);
		hr.getHostingPlayer().sendMessage(ChatColor.RED + p.getDisplayName() + " has left the hosted lobby");
		p.sendMessage(ChatColor.RED + "You have left the hosted lobby");
		p.teleport(t.getExit());
		races.remove(p);
	}

	public static void endHostedRace(Track t) {
		cancelTrackRaces(t, "This hosted session has ended");
		t.setHostedRace(null);
		for (RaceSign sign : t.getSigns()) {
			sign.displayType();
			sign.displayStatus(t.isClosed() ? TrackStatus.CLOSED : TrackStatus.OPEN);
			sign.displayQueue();
		}
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
		races.put(p, vr);
		t.getSigns().forEach(RaceSign::displayQueue);
	}

	public static void removePlayerFromVersus(Track t, Player p) {
		VersusRace race = t.getVersusRace();
		if (race == null || !race.hasPlayer(p)) { return; }
		race.removePlayer(p);
		races.remove(p);
		if (race.playerSize() == 0) {
			RaceManager.finishRace(t);
			t.setVersusRace(null);
		} else {
			t.setVersusRace(race);
		}
	}

	public static void cancelAllRaces(String subtitle) {
		races.keySet().forEach(p -> getRace(p).cancelRace(p, subtitle));
	}

	public static void cancelTrackRaces(Track track, String subtitle) {
		Set<Player> keySet = new HashSet<>(races.keySet());
		keySet.forEach(p -> {
			Race r = getRace(p);
			if (r.getTrack().getName().equals(track.getName())) {
				races.remove(p);
				r.cancelRace(p, subtitle);
			}
		});
	}

	public static Race getRace(Player p) { return races.get(p); }

	public static void cancelRace(Player p, String subtitle) {
		Race race = getRace(p);
		if (race != null) {
			race.cancelRace(p, subtitle);
		}
	}

	public static void removeRace(Player p) {
		races.remove(p);
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
