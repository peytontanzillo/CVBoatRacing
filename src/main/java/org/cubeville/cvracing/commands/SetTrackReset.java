package org.cubeville.cvracing.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.TrackStatus;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SetTrackReset extends BaseCommand {
	JavaPlugin plugin;

	public SetTrackReset(JavaPlugin plugin) {
		super("track tptocp");
		addBaseParameter(new CommandParameterString());
		addBaseParameter(new CommandParameterBoolean());

		setPermission("cvboatrace.track.tptocp");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(CommandSender commandSender, Set<String> set, Map<String, Object> map,
		List<Object> baseParameters) throws CommandExecutionException {
		FileConfiguration config = plugin.getConfig();

		String name = baseParameters.get(0).toString().toLowerCase();
		boolean value = (boolean) baseParameters.get(1);

		if (!config.contains("tracks." + name)) {
			throw new CommandExecutionException("Track with name " + baseParameters.get(0) + " does not exist!");
		}

		config.set("tracks." + name + ".tptocp", value);
		TrackManager.getTrack(name).setIncludeReset(value);

		plugin.saveConfig();
		return new CommandResponse("&aYou have successfully set checkpoint resets for the track " + name + " to " + value + "!");
	}
}
