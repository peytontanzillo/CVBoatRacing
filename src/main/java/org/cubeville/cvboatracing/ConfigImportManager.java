package org.cubeville.cvboatracing;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvboatracing.models.Track;

import java.util.Arrays;
import java.util.List;

public class ConfigImportManager {

	public static void importConfiguration(JavaPlugin plugin) {
		FileConfiguration config = plugin.getConfig();
		if (config.getConfigurationSection("tracks") == null) {
			return;
		}

		for (String trackName : config.getConfigurationSection("tracks").getKeys(false)) {
			Track track = TrackManager.addTrack(trackName);
			ConfigurationSection trackConfig = config.getConfigurationSection("tracks." + trackName);
			String spawn = trackConfig.getString("spawn");
			if (spawn != null) { track.setSpawn( parseLocation(spawn) ); }
			String exit = trackConfig.getString("exit");
			if (exit != null) { track.setExit( parseLocation(exit) ); }
			Boolean isClosed = trackConfig.getBoolean("isClosed");
			if (isClosed == true) { track.setStatus(TrackStatus.CLOSED); }

			List<String> signLocStrings = trackConfig.getStringList("signs");
			for (String signLocString : signLocStrings) {
				Location signLoc = parseLocation(signLocString);
				if (SignManager.signMaterials.contains(signLoc.getBlock().getType())) {
					Sign sign = (Sign) signLoc.getBlock().getState();
					SignManager.addSign(sign, track);
				}
			}

			List<String> cpLocStrings = trackConfig.getStringList("checkpoints");
			for (String cpLocString : cpLocStrings) {
				Location cpLoc = parseLocation(cpLocString);
				if (cpLoc.getBlock().getType() == Material.TRIPWIRE_HOOK) {
					track.addCheckpoint(cpLoc);
				}
			}
		}
	}

	private static Location parseLocation(String s) {
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

}
