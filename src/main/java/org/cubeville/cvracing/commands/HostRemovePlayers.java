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

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostRemovePlayers extends Command {

	public HostRemovePlayers() {
		super("host players remove");
		addBaseParameter(new CommandParameterOnlinePlayer());

		setPermission("cvboatrace.host.players.remove");
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

		Player removingPlayer  = (Player) baseParameters.get(0);

		if (!hostedRace.hasPlayer(removingPlayer)) {
			throw new CommandExecutionException("This lobby does not include " + removingPlayer.getDisplayName() + "!");
		}

		hostedRace.removePlayer(removingPlayer);

		return new CommandResponse("Removed " + player.getDisplayName() + " from the race");
	}
}
