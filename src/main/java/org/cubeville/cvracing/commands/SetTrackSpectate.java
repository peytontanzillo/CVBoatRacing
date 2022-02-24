package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.TrackManager;

import java.util.*;

public class SetTrackSpectate extends Command {
	private JavaPlugin plugin;

	public SetTrackSpectate(JavaPlugin plugin) {
		super("track setspectate");

		addBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.setspectate");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();

		if (!config.contains("tracks." + baseParameters.get(0))) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		String locationsPath = "tracks." + baseParameters.get(0) + ".spectate";

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
		TrackManager.getTrack((String) baseParameters.get(0)).setSpectate(pLoc);
		plugin.saveConfig();

		return new CommandResponse("Set player location as spectate location for the track " + baseParameters.get(0));
	}
}
