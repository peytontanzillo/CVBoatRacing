package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvracing.SignManager;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.RaceSign;
import org.cubeville.cvracing.models.Track;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteSign extends Command {

	private JavaPlugin plugin;

	public DeleteSign(JavaPlugin plugin) {
		super("track signs delete");

		setPermission("cvboatrace.signs.delete");
		this.plugin = plugin;
	}

	@Override
	public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
		throws CommandExecutionException {

		FileConfiguration config = plugin.getConfig();

		Block block = player.getTargetBlock(null, 100);
		if (!(SignManager.signMaterials.contains(block.getType()))) {
			throw new CommandExecutionException("You need to be looking at a sign.");
		}

		RaceSign sign = SignManager.getSign(block.getLocation());
		if (sign == null) {
			throw new CommandExecutionException("The sign you are looking at is not a race sign");
		}

		Location sLoc = block.getLocation();

		List<String> locParameters = Arrays.asList(
				sLoc.getWorld().getName(), // world
				String.valueOf(sLoc.getBlockX()),
				String.valueOf(sLoc.getBlockY()),
				String.valueOf(sLoc.getBlockZ())
		);

		String signsPath = "tracks." + sign.getTrack().getName() + ".signs." + String.join(",", locParameters);
		config.set(signsPath, null);
		SignManager.deleteSign(sLoc);
		sign.getTrack().removeSign(sLoc);

		plugin.saveConfig();

		return new CommandResponse("Successfully deleted a sign for the track " + sign.getTrack().getName() + "!");
	}
}
