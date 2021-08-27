package org.cubeville.cvboatracing.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvboatracing.TrackManager;
import org.cubeville.cvboatracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteLeaderboard extends Command {

	private JavaPlugin plugin;

	public DeleteLeaderboard(JavaPlugin plugin) {
		super("track leaderboards delete");

		addBaseParameter(new CommandParameterString());
		addBaseParameter(new CommandParameterInteger());
		setPermission("cvboatrace.leaderboards.delete");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();
		String name = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(name);

		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		int deletingIndex = (int) baseParameters.get(1);
		if (deletingIndex > track.getLeaderboards().size()) {
			throw new CommandExecutionException("That index does not exist, please use /boatrace track leaderboards list to view the indexes.");
		}
		deletingIndex -= 1;

		String leaderboardsPath = "tracks." + name + ".leaderboards";

		List<String> leaderboardsLocation = config.getStringList(leaderboardsPath);
		leaderboardsLocation.remove(deletingIndex);
		config.set(leaderboardsPath, leaderboardsLocation);

		track.removeLeaderboard(deletingIndex);
		plugin.saveConfig();

		return new CommandResponse("Successfully deleted a leaderboard for the track " + name + "!");
	}
}
