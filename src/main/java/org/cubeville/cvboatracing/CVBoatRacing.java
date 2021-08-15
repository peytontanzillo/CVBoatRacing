package org.cubeville.cvboatracing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvboatracing.commands.*;

public class CVBoatRacing extends JavaPlugin implements Listener {

    private CommandParser commandParser;

    public void onEnable() {
        System.out.println("CVBoatRacing Started!");
        ConfigImportManager.importConfiguration(this);
        RaceManager.setPlugin(this);
        commandParser = new CommandParser();
        commandParser.addCommand(new CreateTrack(this));
        commandParser.addCommand(new SetTrackSpawn(this));
        commandParser.addCommand(new SetTrackExit(this));
        commandParser.addCommand(new CreateRaceSign(this));
        commandParser.addCommand(new OpenTrack(this));
        commandParser.addCommand(new CloseTrack(this));
        commandParser.addCommand(new AddCheckpoint(this));
        commandParser.addCommand(new ListCheckpoints());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventHandlers(), this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("boatrace")) {
            return commandParser.execute(sender, args);
        }
        return false;
    }

}
