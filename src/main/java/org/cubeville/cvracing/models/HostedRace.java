package org.cubeville.cvracing.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.cubeville.cvracing.RaceManager;

public class HostedRace extends VersusRace {

    private Player hostingPlayer;

    public HostedRace(Track track, JavaPlugin plugin, Player hostingPlayer) {
        super(track, plugin, 1);
        this.hostingPlayer = hostingPlayer;
        addPlayerToLobby(hostingPlayer);
    }

    public void addPlayerToLobby(Player p) {
        RaceState rs = new RaceState(p);
        rs.setSpectator(true);
        raceStates.put(p, rs);
    }

    public void removePlayerFromLobby(Player p) {
        if (p.equals(hostingPlayer)) {
            RaceManager.endHostedRace(track);
        }
        raceStates.remove(p);
    }

    public Player getHostingPlayer() {
        return hostingPlayer;
    }

    @Override
    public void cancelRace(Player p, String subtitle) {
        if (p.equals(hostingPlayer)) {
            RaceManager.endHostedRace(track);
        } else if (!raceStates.get(p).isSpectator()) {
            super.cancelRace(p, subtitle);
        }
    }

    @Override
    public void addPlayer(Player p) {
        raceStates.keySet().forEach(player -> player.sendMessage("§b§l" + p.getDisplayName() + " will be racing in the next race!"));
        raceStates.get(p).setSpectator(false);

    }

    @Override
    public void removePlayer(Player p) {
        raceStates.keySet().forEach(player -> player.sendMessage("§b§l" + p.getDisplayName() + " will no longer be racing in the next race."));
        raceStates.get(p).setSpectator(true);
    }

    @Override
    protected void startRace() {
        int i = 0;
        updateSortedRaceStates();
        Scoreboard scoreboard = getRaceScoreboard();
        for (Player p : raceStates.keySet()) {
            if (!raceStates.get(p).isSpectator()) {
                this.setupPlayerOnTrack(p, track.getVersusSpawns().get(i));
                runCountdown(p, 3);
                i++;
            }
            p.setScoreboard(scoreboard);
        }
    }

    @Override
    protected Location endLocation() {
        return track.getSpectate();
    }

    @Override
    protected void endRace() {
        // set everyone to be spectators
        raceStates.values().forEach(rs -> {
            rs.setSpectator(true);
            rs.setCanceled(false);
        });
    }

    @Override
    protected void startLobbyTimeout() {};

    public void setHostingPlayer(Player hostingPlayer) {
        this.hostingPlayer = hostingPlayer;
    }

    public void startCountdown() {
        setLobbyCountdown(5);
    }
}
