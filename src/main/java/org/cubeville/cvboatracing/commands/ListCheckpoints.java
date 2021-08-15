package org.cubeville.cvboatracing.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvboatracing.TrackManager;
import org.cubeville.cvboatracing.models.Track;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListCheckpoints extends BaseCommand {

	public ListCheckpoints() {
		super("track checkpoint list");

		addBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.checkpoints.add");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {

		String name = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(name);
		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}
		List<Location> cps = track.getCheckpoints();

		CommandResponse cr = new CommandResponse();

		for (int i = 0; i < cps.size(); i++) {
			cr.addMessage("&b" + (i + 1) + ": " + cps.get(i).toString());
		}
		return cr;
	}
}
