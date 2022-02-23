package org.cubeville.cvracing.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.TrackType;

import java.util.*;

public class SetTrackType extends Command {
	private JavaPlugin plugin;

	public SetTrackType(JavaPlugin plugin) {
		super("track settype");

		// track name
		addBaseParameter(new CommandParameterString());
		// type
		addBaseParameter(new CommandParameterEnum(TrackType.class));
		setPermission("cvboatrace.track.settype");

		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		String name = baseParameters.get(0).toString().toLowerCase();
		TrackType trackType = (TrackType) baseParameters.get(1);
		FileConfiguration config = plugin.getConfig();

		if (!config.contains("tracks." + name)) {
			throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
		}

		String locationsPath = "tracks." + name + ".type";

		config.set(locationsPath, trackType.toString());
		TrackManager.getTrack(name).setType(trackType);
		plugin.saveConfig();

		return new CommandResponse("Set race type of track " + name + " to be " + trackType);
	}}
