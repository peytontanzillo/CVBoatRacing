package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterOnlinePlayer;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class HostListPlayers extends Command {

	public HostListPlayers() {
		super("host players list");
		setPermission("cvboatrace.host.players.list");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters) throws CommandExecutionException {
		Track track = TrackManager.getTrackHostedBy(player);
		if (track == null) {
			throw new CommandExecutionException("You are not currently hosting a track.");
		}
		HostedRace hostedRace = track.getHostedRace();

		CommandResponse cr = new CommandResponse();
		for (RaceState rs : hostedRace.getRaceStates().values()) {
			if (rs.isSpectator()) { continue; }
			cr.addMessage("&b- " + rs.getPlayer().getDisplayName());
		}
		return cr;
	}
}
