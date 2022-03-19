package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterOnlinePlayer;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.models.HostedRace;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.RaceState;
import org.cubeville.cvracing.models.RaceStateComparator;

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
		Race race = RaceManager.getRace(player);
		if (!(race instanceof HostedRace)) {
			throw new CommandExecutionException("You are not currently hosting a track.");
		}
		HostedRace hostedRace = (HostedRace) race;

		if (!hostedRace.getHostingPlayer().equals(player)) {
			throw new CommandExecutionException("You are not hosting this race!");
		}

		CommandResponse cr = new CommandResponse();
		for (RaceState rs : hostedRace.getRaceStates().values()) {
			if (rs.isSpectator()) { continue; }
			cr.addMessage("&b- " + rs.getPlayer().getDisplayName());
		}
		return cr;
	}
}
