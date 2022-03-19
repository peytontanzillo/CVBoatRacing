package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.models.HostedRace;
import org.cubeville.cvracing.models.Race;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostCountdown extends Command {

	public HostCountdown() {
		super("host countdown");
		setPermission("cvboatrace.host.countdown");
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

		int totalPlayers = (int) hostedRace.getRaceStates().values().stream().filter(x -> !x.isSpectator()).count();
		if (totalPlayers <= 1) {
			throw new CommandExecutionException("There needs to be more than one player to start a race!");
		}

		hostedRace.startCountdown();
		return new CommandResponse("Started countdown");
	}
}
