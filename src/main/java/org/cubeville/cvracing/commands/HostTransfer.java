package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterOnlinePlayer;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.HostedRace;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostTransfer extends Command {

	private final String transferPerm = "cvboatrace.host.transfer";

	public HostTransfer() {
		super("host transfer");
		addBaseParameter(new CommandParameterOnlinePlayer());

		setPermission(transferPerm);
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters) throws CommandExecutionException {
		Track track = TrackManager.getTrackHostedBy(player);
		if (track == null) {
			throw new CommandExecutionException("You are not currently hosting a track.");
		}
		HostedRace hostedRace = track.getHostedRace();

		Player transferringPlayer = (Player) baseParameters.get(0);

		if (!hostedRace.hasPlayer(transferringPlayer)) {
			throw new CommandExecutionException("This lobby does not include " + transferringPlayer.getDisplayName() + "!");
		}

		if (!transferringPlayer.hasPermission(transferPerm)) {
			throw new CommandExecutionException("This player does not have hosting permissions!");
		}

		hostedRace.setHostingPlayer(transferringPlayer);

		return new CommandResponse("Transferred hosting privileges to " + transferringPlayer.getDisplayName());
	}
}
