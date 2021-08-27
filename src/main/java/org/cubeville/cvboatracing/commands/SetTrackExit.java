package org.cubeville.cvboatracing.commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvboatracing.TrackManager;

import java.util.*;

public class SetTrackExit extends Command {
	private JavaPlugin plugin;

	public SetTrackExit(JavaPlugin plugin) {
		super("track setexit");

		addBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.setexit");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();

		if (!config.contains("tracks." + baseParameters.get(0))) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		String locationsPath = "tracks." + baseParameters.get(0) + ".exit";

		Location pLoc = player.getLocation();
		List<String> locParameters = new ArrayList<>(
			Arrays.asList(
				pLoc.getWorld().getName(), // world
				String.valueOf(pLoc.getX()),
				String.valueOf(pLoc.getY()),
				String.valueOf(pLoc.getZ()),
				String.valueOf(pLoc.getYaw()),
				String.valueOf(pLoc.getPitch())
			)
		);

		config.set(locationsPath, String.join(",", locParameters));
		TrackManager.getTrack((String) baseParameters.get(0)).setExit(pLoc);
		plugin.saveConfig();

		return new CommandResponse("Set player location as exit point for the track " + baseParameters.get(0));
	}
}
