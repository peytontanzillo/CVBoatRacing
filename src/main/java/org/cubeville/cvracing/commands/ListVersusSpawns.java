package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Leaderboard;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListVersusSpawns extends BaseCommand {

	public ListVersusSpawns() {
		super("track spawns list versus");

		addBaseParameter(new CommandParameterString());
		setPermission("cvracing.setup.spawns.view");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {

		String name = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(name);
		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}
		List<Location> versusSpawns = track.getVersusSpawns();

		CommandResponse cr = new CommandResponse();

		for (int i = 0; i < versusSpawns.size(); i++) {
			cr.addMessage("&b" + (i + 1) + ": " + formatLeaderboardLocation(versusSpawns.get(i)));
		}
		return cr;
	}

	private String formatLeaderboardLocation(Location loc) {
		return "x: " + loc.getBlockX() +
		" y: " + loc.getBlockY() +
		" z: " + loc.getBlockZ();
	}
}
