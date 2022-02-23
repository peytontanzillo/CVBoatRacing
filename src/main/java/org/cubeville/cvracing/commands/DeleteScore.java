package org.cubeville.cvracing.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.ScoreManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Score;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteScore extends BaseCommand {
	public DeleteScore() {
		super("track scores delete");
		// track
		addBaseParameter(new CommandParameterString());
		// player
		addBaseParameter(new CommandParameterString());
		setPermission("cvboatrace.scores.delete");
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {

		String trackName = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(trackName);

		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		OfflinePlayer op = Bukkit.getOfflinePlayer((String) baseParameters.get(1));
		if (!op.hasPlayedBefore()) {
			throw new CommandExecutionException("Player " + baseParameters.get(1) + " does not exist.");
		}

		Score s = ScoreManager.getScore(op.getUniqueId(), track);
		if (s == null) {
			throw new CommandExecutionException("Player " + op.getName() + " does not have a registered time on " + track.getName() + ".");
		}

		ScoreManager.deleteScore(s);
		track.loadLeaderboards();

		return new CommandResponse("&dSuccessfully deleted time for " + op.getName() + " on track " + track.getName() + ".");
	}
}
