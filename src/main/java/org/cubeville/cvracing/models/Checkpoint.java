package org.cubeville.cvracing.models;


import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Checkpoint {

    private List<CPRegion> regions = new ArrayList<>();
    private List<String> commands = new ArrayList<>();

    public CPRegion getRegionContaining(Player p) {
        for (CPRegion rg : regions) {
            if (rg.containsPlayer(p)) { return rg; }
        }
        return null;
    }

    public CPRegion addRegion(Location min, Location max) {
        CPRegion cpRegion = new CPRegion(min, max);
        regions.add(cpRegion);
        return cpRegion;
    }

    public void removeRegion(int index) {
        regions.remove(index);
    }

    public List<CPRegion> getRegions() {
        return regions;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void addCommand(String command) {
        commands.add(command);
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public void removeCommand(int index) { commands.remove(index); }
}
