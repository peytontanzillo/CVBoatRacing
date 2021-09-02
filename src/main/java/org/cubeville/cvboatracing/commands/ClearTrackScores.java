package org.cubeville.cvboatracing.commands;

import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvboatracing.ScoreManager;
import org.cubeville.cvboatracing.TrackManager;
import org.cubeville.cvboatracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClearTrackScores extends BaseCommand {

	public ClearTrackScores() {
		super("track scores clear");
		// track
		addBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.scores.clear");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {

		String trackName = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(trackName);

		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}
		ScoreManager.deleteAllScores(track);
		track.loadLeaderboards();
		return new CommandResponse("&dThe scores on track " + trackName + " are cleared.");
	}
}
