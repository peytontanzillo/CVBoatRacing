package org.cubeville.cvracing.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostStart extends Command {

	public HostStart() {
		super("host start");
		// track name
		addBaseParameter(new CommandParameterString());
		setPermission("cvracing.host.start");
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters) throws CommandExecutionException {
		String name = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(name);

		if (track == null) {
			throw new CommandExecutionException("Track " + name + " does not exist.");
		}

		if (track.getHostedRace() != null) {
			throw new CommandExecutionException("Track " + name + " is already being hosted by " + track.getHostedRace().getHostingPlayer().getDisplayName());
		}

		if (track.isClosed()) {
			throw new CommandExecutionException("Track " + name + " is closed, so it cannot be hosted");
		}

		if (track.getSpectate() == null) {
			throw new CommandExecutionException("Track " + name + " needs a spectating area to host");
		}

		if (track.getVersusSpawns().size() == 0) {
			throw new CommandExecutionException("Track " + name + " is not set up for hosting");
		}

		track.clearQueue();
		RaceManager.addHostedRace(track, player);

		return new CommandResponse("&aStarted hosting the track " + name + "!");
	}
}
