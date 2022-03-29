package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.TrackManager;

import java.util.*;

public class SetTrialsSpawn extends Command {
	private JavaPlugin plugin;

	public SetTrialsSpawn(JavaPlugin plugin) {
		super("track spawns set trials");

		addBaseParameter(new CommandParameterString());
		setPermission("cvracing.setup.spawns.edit");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		String name = baseParameters.get(0).toString().toLowerCase();
		FileConfiguration config = plugin.getConfig();

		if (!config.contains("tracks." + name)) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		String locationsPath = "tracks." + name + ".trialsSpawn";

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
		TrackManager.getTrack(name).setTrialsSpawn(pLoc);
		plugin.saveConfig();

		return new CommandResponse("Set player location as spawn point for the track " + baseParameters.get(0));
	}}
