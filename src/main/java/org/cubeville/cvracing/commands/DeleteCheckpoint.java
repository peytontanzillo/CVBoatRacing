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
		if (deletingIndex > track.getCheckpoints().size() || deletingIndex <= 0) {
			throw new CommandExecutionException("Index " + deletingIndex +" does not exist, please use /race track checkpoints list to view the indexes.");
		}

		deletingIndex -= 1;
		String locationsPath = "tracks." + name + ".checkpoints.";

		if (track.getCheckpoints().size() > 1) {
			for (int i = deletingIndex; i < track.getCheckpoints().size() - 1; i++) {
				config.set(locationsPath + deletingIndex, config.get(locationsPath + (deletingIndex + 1)));
			}
		}
		config.set(locationsPath + (track.getCheckpoints().size() - 1), null);

		track.removeCheckpoint(deletingIndex);
		plugin.saveConfig();

		return new CommandResponse("Successfully deleted a checkpoint for the track " + name + "!");
	}
}
