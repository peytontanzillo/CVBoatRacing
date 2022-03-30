package org.cubeville.cvracing.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Checkpoint;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteCheckpointCommand extends Command {

	private JavaPlugin plugin;

	public DeleteCheckpointCommand(JavaPlugin plugin) {
		super("track checkpoints commands delete");
		// track id
		addBaseParameter(new CommandParameterString());
		// cp index
		addBaseParameter(new CommandParameterInteger());
		// deleting index
		addBaseParameter(new CommandParameterInteger());

		setPermission("cvracing.admin.cpcommands.edit");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();
		String name = baseParameters.get(0).toString().toLowerCase();
		int cpIndex = (int) baseParameters.get(1);
		Track track = TrackManager.getTrack(name);

		if (track == null) {
			throw new CommandExecutionException("Track " + name + " does not exist.");
		}

		Checkpoint cp = track.getCheckpoints().get(cpIndex - 1);
		if (cp == null) {
			throw new CommandExecutionException("Index " + cpIndex + " does not exist, please use /race track checkpoints list to view the indexes.");
		}

		int deletingIndex = (int) baseParameters.get(2);
		if (deletingIndex > cp.getCommands().size() || deletingIndex < 1) {
			throw new CommandExecutionException("That command index does not exist, please use /boatrace track checkpoints commands list to view the indexes.");
		}
		String locationsPath = "tracks." + name + ".checkpoints." + (cpIndex - 1) + ".variables.commands";
		cp.removeCommand(deletingIndex - 1);
		config.set(locationsPath, cp.getCommands());

		plugin.saveConfig();

		return new CommandResponse("Successfully deleted checkpoint command " + deletingIndex + " on checkpoint " + cpIndex + " of track " + name + "!");
	}
}
