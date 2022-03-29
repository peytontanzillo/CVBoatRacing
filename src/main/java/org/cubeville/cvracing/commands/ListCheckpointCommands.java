package org.cubeville.cvracing.commands;

import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Checkpoint;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListCheckpointCommands extends BaseCommand {

	public ListCheckpointCommands() {
		super("track checkpoints commands list");
		// track
		addBaseParameter(new CommandParameterString());
		// cp id
		addBaseParameter(new CommandParameterInteger());
		setPermission("cvracing.setup.cpcommands.view");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {

		String name = baseParameters.get(0).toString().toLowerCase();
		int cpIndex = (int) baseParameters.get(1);

		Track track = TrackManager.getTrack(name);
		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		Checkpoint cp = track.getCheckpoints().get(cpIndex - 1);
		if (cp == null) {
			throw new CommandExecutionException("Index " + cpIndex + " does not exist, please use /race track checkpoints list to view the indexes.");
		}

		CommandResponse cr = new CommandResponse();
		for (int i = 0; i < cp.getCommands().size(); i++) {
			cr.addMessage("&b" + (i + 1) + ": " + cp.getCommands().get(i));
		}
		return cr;
	}
}
