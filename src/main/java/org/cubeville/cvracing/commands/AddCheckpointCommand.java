package org.cubeville.cvracing.commands;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.commons.commands.*;
import org.cubeville.commons.utils.BlockUtils;
import org.cubeville.cvracing.TrackManager;
import org.cubeville.cvracing.models.CPRegion;
import org.cubeville.cvracing.models.Checkpoint;
import org.cubeville.cvracing.models.Track;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddCheckpointCommand extends Command {

    private JavaPlugin plugin;

    public AddCheckpointCommand(JavaPlugin plugin) {
        super("track checkpoints commands add");

        // track
        addBaseParameter(new CommandParameterString());
        // int of cp
        addBaseParameter(new CommandParameterInteger());
        // command to run
        addBaseParameter(new CommandParameterString());


        setPermission("cvracing.admin.cpcommands.edit");
        this.plugin = plugin;
    }

    @Override
    public CommandResponse execute(Player player, Set<String> set, Map<String, Object> map, List<Object> baseParameters)
            throws CommandExecutionException {

        FileConfiguration config = plugin.getConfig();
        String name = baseParameters.get(0).toString().toLowerCase();
        Track track = TrackManager.getTrack(name);
        int cpIndex = (int) baseParameters.get(1);
        String command = (String) baseParameters.get(2);

        if (!config.contains("tracks." + name)) {
            throw new CommandExecutionException("Track " + baseParameters.get(0) + " does not exist.");
        }

        Checkpoint cp = track.getCheckpoints().get(cpIndex - 1);
        if (cp == null) {
            throw new CommandExecutionException("Index " + cpIndex + " does not exist, please use /race track checkpoints list to view the indexes.");
        }

        String locationsPath = "tracks." + name + ".checkpoints." + (cpIndex - 1) + ".variables.commands";
        cp.addCommand(command);
        config.set(locationsPath, cp.getCommands());

        plugin.saveConfig();

        return new CommandResponse("Successfully added a command to checkpoint " + cpIndex + " on track " + track.getName() + "!");
    }
}
