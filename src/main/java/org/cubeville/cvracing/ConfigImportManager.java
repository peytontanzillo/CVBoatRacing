package org.cubeville.cvracing;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvracing.models.Track;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ConfigImportManager {

	public static void importConfiguration(JavaPlugin plugin) {
		FileConfiguration config = plugin.getConfig();
		if (config.getConfigurationSection("tracks") == null) {
			return;
		}

		for (String trackName : Objects.requireNonNull(config.getConfigurationSection("tracks")).getKeys(false)) {
			Track track = TrackManager.addTrack(trackName);
			ConfigurationSection trackConfig = config.getConfigurationSection("tracks." + trackName);
			assert trackConfig != null;
			String spawn = trackConfig.getString("spawn");
			if (spawn != null) { track.setSpawn( parseTeleportLocation(spawn) ); }
			String exit = trackConfig.getString("exit");
			if (exit != null) { track.setExit( parseTeleportLocation(exit) ); }
			String type = trackConfig.getString("type");
			if (type != null) {
				try { TrackType tt = TrackType.valueOf(type.toUpperCase()); track.setType(tt); }
				// don't worry if the type doesn't match, just use the default race type (boats)
				catch (IllegalArgumentException e) {}
			}
			boolean isClosed = trackConfig.getBoolean("isClosed");
			if (isClosed) { track.setStatus(TrackStatus.CLOSED); }

			List<String> signLocStrings = trackConfig.getStringList("signs");
			for (String signLocString : signLocStrings) {
				Location signLoc = parseBlockLocation(signLocString);
				if (SignManager.signMaterials.contains(signLoc.getBlock().getType())) {
					Sign sign = (Sign) signLoc.getBlock().getState();
					SignManager.addSign(sign, track);
				}
			}

			List<String> cpLocStrings = trackConfig.getStringList("checkpoints");
			for (String cpLocString : cpLocStrings) {
				Location cpLoc = parseBlockLocation(cpLocString);
				if (RaceManager.checkpointItemTypes.contains(cpLoc.getBlock().getType())) {
					if (cpLoc.getBlock().getType() != Material.TRIPWIRE_HOOK) {
						System.out.println("Passed CP kill");
					}
					track.addCheckpoint(cpLoc);
				}
			}

			List<String> leaderboardLocStrings = trackConfig.getStringList("leaderboards");
			for (String lbLocString : leaderboardLocStrings) {
				Location lbLoc = parseTeleportLocation(lbLocString);
				List<Entity> nearbyEntities = (List<Entity>) Objects.requireNonNull(lbLoc.getWorld())
					.getNearbyEntities(lbLoc, 2, 5, 2);

				// fail safe if any of the armor stands make it to enabling of the plugin
				for (Entity ent : nearbyEntities) {
					if (ent.getScoreboardTags().contains("CVBoatRace-LeaderboardArmorStand")) {
						ent.remove();
					}
				}
				track.addLeaderboard(lbLoc);
			}
		}
	}

	private static Location parseBlockLocation(String s) {
		List<String> params = Arrays.asList(s.split(","));
		return new Location(
			Bukkit.getWorld(params.get(0)), // world
			Integer.parseInt(params.get(1)), // x
			Integer.parseInt(params.get(2)), // y
			Integer.parseInt(params.get(3)), // z
			Float.parseFloat(params.get(4)), // pitch
			Float.parseFloat(params.get(5)) // yaw
		);
	}

	private static Location parseTeleportLocation(String s) {
		List<String> params = Arrays.asList(s.split(","));
		return new Location(
			Bukkit.getWorld(params.get(0)), // world
			Float.parseFloat(params.get(1)), // x
			Float.parseFloat(params.get(2)), // y
			Float.parseFloat(params.get(3)), // z
			Float.parseFloat(params.get(4)), // pitch
			Float.parseFloat(params.get(5)) // yaw
		);
	}
}
