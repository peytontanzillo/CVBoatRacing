package org.cubeville.cvracing.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Track;

import java.util.*;

public class DeleteCheckpoint extends Command {

	private JavaPlugin plugin;

	public DeleteCheckpoint(JavaPlugin plugin) {
		super("track checkpoints delete");

		addBaseParameter(new CommandParameterString());
		addBaseParameter(new CommandParameterInteger());
		setPermission("cvboatrace.checkpoints.delete");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();
		String name = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(name);

		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		int deletingIndex = (int) baseParameters.get(1);
		if (deletingIndex > track.getCheckpoints().size()) {
			throw new CommandExecutionException("That index does not exist, please use /boatrace track checkpoints list to view the indexes.");
		}
		deletingIndex -= 1;

		String locationsPath = "tracks." + name + ".checkpoints";

		List<String> twLocations = config.getStringList(locationsPath);
		twLocations.remove(deletingIndex);
		config.set(locationsPath, twLocations);

		track.removeCheckpoint(deletingIndex);
		plugin.saveConfig();

		return new CommandResponse("Successfully deleted a checkpoint for the track " + name + "!");
	}
}
