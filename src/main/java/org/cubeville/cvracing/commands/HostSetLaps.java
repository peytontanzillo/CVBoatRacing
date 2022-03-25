package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterInteger;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.HostedRace;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.RaceState;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostSetLaps extends Command {

	public HostSetLaps() {
		super("host laps");
		addBaseParameter(new CommandParameterInteger());
		setPermission("cvboatrace.host.laps");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters) throws CommandExecutionException {
		Track track = TrackManager.getTrackHostedBy(player);
		if (track == null) {
			throw new CommandExecutionException("You are not currently hosting a track.");
		}
		HostedRace hostedRace = track.getHostedRace();

		int lapCount = (int) baseParameters.get(0);
		if (lapCount < 1) {
			throw new CommandExecutionException("Lap count needs to be greater than or equal to 1");
		}
		hostedRace.setLaps(lapCount);

		return new CommandResponse("Lap count has been set to " + lapCount + "!");
	}
}
