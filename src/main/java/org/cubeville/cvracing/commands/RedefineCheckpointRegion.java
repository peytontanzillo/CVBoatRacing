package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.cvracing.RaceUtilities;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.CPRegion;
import org.cubeville.cvracing.models.Checkpoint;
import org.cubeville.cvracing.models.Track;

import java.util.*;

public class RedefineCheckpointRegion extends Command {

	private JavaPlugin plugin;

	public RedefineCheckpointRegion(JavaPlugin plugin) {
		super("track checkpoints regions redefine");

		// track
		addBaseParameter(new CommandParameterString());
		// cp id
		addBaseParameter(new CommandParameterInteger());
		// rg id
		addBaseParameter(new CommandParameterInteger());

		setPermission("cvracing.setup.cps.edit");
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

		Location min, max;

		try {
			min = BlockUtils.getWESelectionMin(player);
			max = BlockUtils.getWESelectionMax(player).add(1.0, 1.0, 1.0);
		}
		catch(IllegalArgumentException e) {
			throw new CommandExecutionException("Please make a cuboid worldedit selection before running this command.");
		}

		int cpIndex = (int) baseParameters.get(1);
		if (cpIndex > track.getCheckpoints().size() || cpIndex <= 0) {
			throw new CommandExecutionException("That checkpoint index does not exist, please use /race track checkpoints list to view the indexes.");
		}
		cpIndex -= 1;

		Checkpoint cp = track.getCheckpoints().get(cpIndex);

		int rgIndex = (int) baseParameters.get(2);
		if (rgIndex > cp.getRegions().size() || rgIndex <= 0) {
			throw new CommandExecutionException("That region index does not exist, please use /race track checkpoints list to view the indexes.");
		}
		rgIndex -= 1;

		CPRegion cpRegion = cp.getRegions().get(rgIndex);

		String cpLocationsPath = "tracks." + name + ".checkpoints." + cpIndex;

		// remove the previous region
		config.set(cpLocationsPath + "." + cpRegion.getString(), null);

		cpRegion.defineRegion(min, max);

		if (cpRegion.getReset() == null) {
			config.createSection(cpLocationsPath + "." + cpRegion.getString());
		} else {
			config.set(cpLocationsPath + "." + cpRegion.getString() + ".reset", RaceUtilities.tpLocToString(cpRegion.getReset()));
		}

		track.getCheckpoints().get(cpIndex).addRegion(min, max);
		plugin.saveConfig();

		return new CommandResponse("Successfully redefined checkpoint region for the track " + name + "!");
	}
}
