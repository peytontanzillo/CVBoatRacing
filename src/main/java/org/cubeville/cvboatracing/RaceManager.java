package org.cubeville.cvboatracing;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvboatracing.models.Race;
import org.cubeville.cvboatracing.models.RaceSign;
import org.cubeville.cvboatracing.models.Track;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RaceManager {

	private static HashMap<UUID, Race> races = new HashMap<>();
	private static JavaPlugin plugin;

	public static void setPlugin(JavaPlugin javaPlugin) {
		plugin = javaPlugin;
	}

	public static Race addRace(Track t, Player p) {
		Race race = new Race(t, p, plugin);
		races.put(p.getUniqueId(), race);
		return race;
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
		Player p = t.getPlayerFromQueue();
		if (p != null) {
			int queueSize = t.getQueue().size();
			for (RaceSign sign : t.getSigns()) {
				sign.displayQueue(queueSize);
			}
			addRace(t, p);
		}
	}

}
