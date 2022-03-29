package org.cubeville.cvracing.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.TrackManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CreateTrack extends BaseCommand {

	private JavaPlugin plugin;

	public CreateTrack(JavaPlugin plugin) {
		super("track create");
		addBaseParameter(new CommandParameterString());
		setPermission("cvracing.setup.create");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {
		FileConfiguration config = plugin.getConfig();

		String name = baseParameters.get(0).toString().toLowerCase();

		if (config.contains("tracks." + name)) {
			throw new CommandExecutionException("Track with name " + baseParameters.get(0) + " already exists!");
		}

		config.createSection("tracks." + name);
		TrackManager.addTrack(name);
		plugin.saveConfig();
		return new CommandResponse("&aCreated the track " + baseParameters.get(0) + "!");
	}
}
