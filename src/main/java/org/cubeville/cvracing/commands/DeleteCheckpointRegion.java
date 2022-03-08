package org.cubeville.cvracing.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.CPRegion;
import org.cubeville.cvracing.models.Checkpoint;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteCheckpointRegion extends Command {

	private JavaPlugin plugin;

	public DeleteCheckpointRegion(JavaPlugin plugin) {
		super("track checkpoints regions delete");

		addBaseParameter(new CommandParameterString());
		addBaseParameter(new CommandParameterInteger());
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

		int cpIndex = (int) baseParameters.get(1);
		if (cpIndex > track.getCheckpoints().size() || cpIndex <= 0) {
			throw new CommandExecutionException("Checkpoint index " + cpIndex +" does not exist, please use /race track checkpoints list to view the indexes.");
		}
		cpIndex -= 1;
		Checkpoint cp = track.getCheckpoints().get(cpIndex);

		int deletingRGIndex = (int) baseParameters.get(2);
		if (deletingRGIndex > cp.getRegions().size() || deletingRGIndex <= 0) {
			throw new CommandExecutionException("Region index " + deletingRGIndex +" does not exist, please use /race track checkpoints list to view the indexes.");
		}
		deletingRGIndex -= 1;

		CPRegion cpRegion = cp.getRegions().get(deletingRGIndex);

		String locationsPath = "tracks." + name + ".checkpoints." + cpIndex + "." + cpRegion.getString();
		config.set(locationsPath, null);
		cp.removeRegion(deletingRGIndex);

		plugin.saveConfig();

		return new CommandResponse("Successfully deleted a checkpoint region for the track " + name + "!");
	}
}
