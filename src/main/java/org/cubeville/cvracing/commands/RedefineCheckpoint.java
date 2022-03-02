package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Checkpoint;
import org.cubeville.cvracing.models.Track;

import java.util.*;

public class RedefineCheckpoint extends Command {

	private JavaPlugin plugin;

	public RedefineCheckpoint(JavaPlugin plugin) {
		super("track checkpoints redefine");

		addBaseParameter(new CommandParameterString());
		addBaseParameter(new CommandParameterInteger());

		setPermission("cvboatrace.checkpoints.redefine");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();
		String name = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(name);

		if (!config.contains("tracks." + name)) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		int redefineIndex = (int) baseParameters.get(1);
		if (redefineIndex > track.getCheckpoints().size() || redefineIndex <= 0) {
			throw new CommandExecutionException("That index does not exist, please use /race track checkpoints list to view the indexes.");
		}
		redefineIndex -= 1;

		Location min, max;

		try {
			min = BlockUtils.getWESelectionMin(player);
			max = BlockUtils.getWESelectionMax(player).add(1.0, 1.0, 1.0);
		}
		catch(IllegalArgumentException e) {
			throw new CommandExecutionException("Please make a cuboid worldedit selection before running this command.");
		}

		String locationsPath = "tracks." + name + ".checkpoints." + redefineIndex;

		config.set(locationsPath + ".min", locToString(min));
		config.set(locationsPath + ".max", locToString(max));

		track.getCheckpoints().get(redefineIndex).defineBounds(min, max);
		plugin.saveConfig();

		return new CommandResponse("Successfully created a checkpoint for the track " + baseParameters.get(0) + "!");
	}

	private String locToString(Location loc) {
		List<String> locParameters = new ArrayList<>(
				Arrays.asList(
						loc.getWorld().getName(), // world
						String.valueOf(loc.getBlockX()),
						String.valueOf(loc.getBlockY()),
						String.valueOf(loc.getBlockZ())
				)
		);
		return String.join(",", locParameters);
	}
}
