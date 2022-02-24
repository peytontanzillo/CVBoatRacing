package org.cubeville.cvracing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.cubeville.commons.commands.CommandParser;
import org.cubeville.cvracing.commands.*;
import org.cubeville.cvracing.dbfiles.RacingDB;

import java.io.IOException;

public class CVRacing extends JavaPlugin implements Listener {

    private CommandParser commandParser;
    private RacingDB db;

    public void onEnable() {
        System.out.println("CVBoatRacing Started!");

        // database setup
        this.db = new RacingDB(this);
        try {
            this.db.createBackup(this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.db.load();

        RaceManager.setPlugin(this);

        // import from config
        ConfigImportManager.importConfiguration(this);

        // import data from db
        ScoreManager.importDataFromDatabase(this.db);

        // load leaderboards
        TrackManager.loadLeaderboards();

        //commands
        commandParser = new CommandParser();
        commandParser.addCommand(new CreateTrack(this));
        commandParser.addCommand(new SetTrackSpawn(this));
        commandParser.addCommand(new SetTrackExit(this));
        commandParser.addCommand(new SetTrackSpectate(this));
        commandParser.addCommand(new SetTrackType(this));
        commandParser.addCommand(new CreateRaceSign(this));
        commandParser.addCommand(new OpenTrack(this));
        commandParser.addCommand(new CloseTrack(this));
        commandParser.addCommand(new AddCheckpoint(this));
        commandParser.addCommand(new AddLeaderboard(this));
        commandParser.addCommand(new DeleteCheckpoint(this));
        commandParser.addCommand(new DeleteTrack(this));
        commandParser.addCommand(new DeleteSign(this));
        commandParser.addCommand(new DeleteLeaderboard(this));
        commandParser.addCommand(new ListCheckpoints());
        commandParser.addCommand(new GetPB());
        commandParser.addCommand(new GetLeaderboard());
        commandParser.addCommand(new UseSplits());
        commandParser.addCommand(new GetRank());
        commandParser.addCommand(new ClearPlayerScores());
        commandParser.addCommand(new ClearTrackScores());
        commandParser.addCommand(new DeleteScore());
        commandParser.addCommand(new ListSigns());
        commandParser.addCommand(new ListLeaderboards());
        commandParser.addCommand(new CompareSplits());
        commandParser.addCommand(new Help());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EventHandlers(this), this);
    }

    public void onDisable() {
        this.db.disconnect();
        TrackManager.clearArmorStands();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("race")) {
            return commandParser.execute(sender, args);
        }
        return false;
    }

}
