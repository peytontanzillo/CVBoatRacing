package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.RaceUtilities;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.CPRegion;
import org.cubeville.cvracing.models.Checkpoint;
import org.cubeville.cvracing.models.Track;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListCheckpoints extends BaseCommand {

	public ListCheckpoints() {
		super("track checkpoints list");

		addBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.checkpoints.list");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {

		String name = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(name);
		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}
		List<Checkpoint> cps = track.getCheckpoints();

		CommandResponse cr = new CommandResponse();

		for (int i = 0; i < cps.size(); i++) {
			Checkpoint cp = cps.get(i);
			cr.addMessage("&eCheckpoint " + (i + 1) + " regions:");
			for (int j = 0; j < cp.getRegions().size(); j++) {
				CPRegion cpRegion = cp.getRegions().get(j);
				cr.addMessage((j + 1) + ": min: " + RaceUtilities.formatBlockLocation(cpRegion.getMin()) + " max: " + RaceUtilities.formatBlockLocation(cpRegion.getMax()));
			}
		}
		return cr;
	}
}
