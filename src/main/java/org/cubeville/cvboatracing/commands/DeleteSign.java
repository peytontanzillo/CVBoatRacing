package org.cubeville.cvboatracing.commands;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.cvboatracing.SignManager;
import org.cubeville.cvboatracing.TrackManager;
import org.cubeville.cvboatracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeleteSign extends Command {

	private JavaPlugin plugin;

	public DeleteSign(JavaPlugin plugin) {
		super("track signs delete");

		addBaseParameter(new CommandParameterString());
		addBaseParameter(new CommandParameterInteger());
		setPermission("cvboatrace.signs.delete");
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
		if (deletingIndex > track.getSigns().size()) {
			throw new CommandExecutionException("That index does not exist, please use /boatrace track signs list to view the indexes.");
		}
		deletingIndex -= 1;

		String signsPath = "tracks." + name + ".signs";

		List<String> signLocations = config.getStringList(signsPath);
		signLocations.remove(deletingIndex);
		config.set(signsPath, signLocations);

		SignManager.deleteSign(track.getSigns().get(deletingIndex).getSign().getLocation());

		track.removeSign(deletingIndex);
		plugin.saveConfig();

		return new CommandResponse("Successfully deleted a sign for the track " + name + "!");
	}
}
