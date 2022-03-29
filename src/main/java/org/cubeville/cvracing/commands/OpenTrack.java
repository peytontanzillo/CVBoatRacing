package org.cubeville.cvracing.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.BaseCommand;
import org.cubeville.commons.commands.CommandExecutionException;
import org.cubeville.commons.commands.CommandParameterString;
import org.cubeville.commons.commands.CommandResponse;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.TrackStatus;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OpenTrack extends BaseCommand {
	JavaPlugin plugin;

	public OpenTrack(JavaPlugin plugin) {
		super("track open");
		addBaseParameter(new CommandParameterString());
		setPermission("cvracing.admin.open");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {
		FileConfiguration config = plugin.getConfig();

		String name = baseParameters.get(0).toString().toLowerCase();

		if (!config.contains("tracks." + name)) {
			throw new CommandExecutionException("Track with name " + name + " does not exist!");
		}

		config.set("tracks." + name + ".isClosed", false);
		Track track = TrackManager.getTrack(name);
		track.setStatus(TrackStatus.OPEN);
		track.setClosed(false);

		plugin.saveConfig();
		return new CommandResponse("&aThe track " + name + " has been reopened!");
	}
}
