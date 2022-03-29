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

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetCheckpointRegionReset extends Command {

	private JavaPlugin plugin;

	public SetCheckpointRegionReset(JavaPlugin plugin) {
		super("track checkpoints regions setreset");

		// track
		addBaseParameter(new CommandParameterString());
		// cp id
		addBaseParameter(new CommandParameterInteger());
		// rg id
		addBaseParameter(new CommandParameterInteger());

		setPermission("cvracing.setup.rgreset");
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

		String cpResetPath = "tracks." + name + ".checkpoints." + cpIndex + "." + cpRegion.getString() + ".reset";
		config.set(cpResetPath, RaceUtilities.tpLocToString(player.getLocation()));
		cpRegion.setReset(player.getLocation());
		plugin.saveConfig();

		return new CommandResponse("Successfully set the reset point for region " + (rgIndex + 1) +" of checkpoint " + (cpIndex + 1) + " on track " + name + "!");
	}
}
