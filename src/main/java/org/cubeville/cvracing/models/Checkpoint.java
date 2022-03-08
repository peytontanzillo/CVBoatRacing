package org.cubeville.cvracing.models;


import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Checkpoint {

    private List<CPRegion> regions = new ArrayList<>();
    private String command;

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

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
