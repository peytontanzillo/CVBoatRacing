package org.cubeville.cvracing.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.HostedRace;
import org.cubeville.cvracing.models.Race;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class HostAnnounce extends Command {

	public HostAnnounce() {
		super("host announce");
		setPermission("cvboatrace.host.announce");
		addOptionalBaseParameter(new CommandParameterInteger());
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters) throws CommandExecutionException {
		Track track = TrackManager.getTrackHostedBy(player);
		if (track == null) {
			throw new CommandExecutionException("You are not currently hosting a track.");
		}
		HostedRace hostedRace = track.getHostedRace();

		String announceString = "§6[§eCVRacing§6]§b A hosted " + hostedRace.getTrack().getType().toString().toLowerCase() + " race on track " + hostedRace.getTrack().getName() + " will be starting ";
		if (baseParameters.size() == 0) {
			announceString += "soon!";
		} else {
			int minutes = (int) baseParameters.get(0);
			if (minutes <= 0) {
				throw new CommandExecutionException("The minutes provided must be greater than or equal to 1");
			} else if (minutes == 1) {
				announceString += "in 1 minute!";
			} else {
				announceString += "in " + minutes + " minutes!";
			}
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "runalias /announceracehost " + announceString);
		return new CommandResponse("");
	}
}
