package org.cubeville.cvracing.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteTrack extends BaseCommand {
	private JavaPlugin plugin;
	public DeleteTrack(JavaPlugin plugin) {
		super("track delete");
		// track
		addBaseParameter(new CommandParameterString());
		setPermission("cvracing.setup.delete");

		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();

		String trackName = baseParameters.get(0).toString().toLowerCase();
		Track track = TrackManager.getTrack(trackName);

		if (track == null) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		config.set("tracks." + trackName, null);
		plugin.saveConfig();
		track.clearLeaderboards();
		TrackManager.deleteTrack(track);
		return new CommandResponse("&d Track " + track.getName() + " and its scores has been deleted.");
	}
}

