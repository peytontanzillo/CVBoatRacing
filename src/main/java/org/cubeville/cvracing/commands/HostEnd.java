package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.Command;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.HostedRace;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostEnd extends Command {

	public HostEnd() {
		super("host end");
		setPermission("cvboatrace.host.end");
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

		RaceManager.endHostedRace(hostedRace.getTrack());
		player.teleport(hostedRace.getTrack().getExit());

		return new CommandResponse("&aEnded hosting session on the track " + hostedRace.getTrack().getName() + "!");
	}
}
