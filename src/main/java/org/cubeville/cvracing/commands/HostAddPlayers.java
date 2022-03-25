package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.HostedRace;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HostAddPlayers extends Command {

	public HostAddPlayers() {
		super("host players add");
		addBaseParameter(new CommandParameterOnlinePlayer());

		setPermission("cvboatrace.host.players.add");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters) throws CommandExecutionException {
		Track track = TrackManager.getTrackHostedBy(player);
		if (track == null) {
			throw new CommandExecutionException("You are not currently hosting a track.");
		}
		HostedRace hostedRace = track.getHostedRace();

		Player addingPlayer = (Player) baseParameters.get(0);

		if (!hostedRace.hasPlayer(addingPlayer)) {
			throw new CommandExecutionException("This lobby does not include " + addingPlayer.getDisplayName() + "!");
		}

		int totalPlayers = (int) hostedRace.getRaceStates().values().stream().filter(x -> !x.isSpectator()).count();
		if (totalPlayers >= hostedRace.getTrack().getVersusSpawns().size()) {
			throw new CommandExecutionException("This track is full!");
		}

		hostedRace.addPlayer(addingPlayer);

		return new CommandResponse("Added " + addingPlayer.getDisplayName() + " to the race");
	}
}
