package org.cubeville.cvracing.models;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.RaceUtilities;
import org.cubeville.cvracing.TrackStatus;

import java.util.ArrayList;
import java.util.List;

public class VersusRace extends Race {
    List<Player> players = new ArrayList<>();
    List<Player> finishedPlayers = new ArrayList<>();
    private int lobbyTimeout;
    private int lobbyCountdown = 0;
    private int countdownValue;
    public int maxPlayers;
    // I'm stopping at 30th, I do not like places lol
    private final String[] placeStrings = {
            "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th",
            "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th",
            "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th", "30th"
    };

    final int LOBBY_TIMEOUT_MINUTES = 1;

    public VersusRace(Track track, JavaPlugin plugin) {
        super(track, plugin);
        this.maxPlayers = track.getVersusSpawns().size();
        startLobbyTimeout();
    }

    public void addPlayer(Player p) {
        players.add(p);
        players.forEach(player -> player.sendMessage("§e" + p.getDisplayName() + "§6 has joined the race lobby"));

        if (playerSize() > 1 && lobbyCountdown == 0) {
            setLobbyCountdown(20);
        }

        if (playerSize() >= maxPlayers) {
            players.forEach(player -> player.sendMessage("§b§lThis lobby is now full! Starting countdown."));
            if (countdownValue > 5) {
                setLobbyCountdown(5);
            }
        }
    }

    private void cancelLobbyTimeout() {
        if (lobbyTimeout != 0) {
            Bukkit.getScheduler().cancelTask(lobbyTimeout);
            lobbyTimeout = 0;
        }
    }

    private void startLobbyTimeout() {
        lobbyTimeout = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (Player p : players) {
                p.sendMessage("§cThe queue for this race has timed out.");
            }
            RaceManager.finishRace(track);
        }, LOBBY_TIMEOUT_MINUTES * 60000);
    }

    private void setLobbyCountdown(int time) {
        cancelLobbyCountdown();
        countdownValue = time;
        lobbyCountdown =  Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            if (countdownValue == 0) {
                cancelLobbyCountdown();
                startVersusRace();
            } else if (countdownValue <= 5) {
                players.forEach(player -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 2F, 1F);
                    player.sendMessage("§bRace starting in " + countdownValue + "...");
                });
            } else if (countdownValue % 15 == 0) {
                players.forEach(player -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 2F, 1F);
                    player.sendMessage("§bRace starting in " + countdownValue + " seconds");
                });
            }
            countdownValue--;
        }, 0L, 20L);
    }

    private void cancelLobbyCountdown() {
        if (lobbyCountdown != 0) {
            Bukkit.getScheduler().cancelTask(lobbyCountdown);
            lobbyCountdown = 0;
        }
    }

    public void removePlayer(Player p) {
        players.remove(p);
        switch (playerSize()) {
            case 1:
                startLobbyTimeout();
                break;
            case 0:
                cancelLobbyTimeout();
                break;
        }
    }

    private void startVersusRace() {
        this.getTrack().setStatus(TrackStatus.IN_USE);
        for (int i = 0; i < playerSize(); i++) {
            this.setupPlayerOnTrack(players.get(i), track.getVersusSpawns().get(i));
            runCountdown(players.get(i), 3);
        }
    }

    public boolean hasPlayer(Player p) {
        return players.contains(p);
    }

    public int playerSize() {
        return players.size();
    }

    @Override
    public void completeRace(Player p) {
        long elapsed = this.raceStates.get(p).getElapsed();
        int finishIndex = finishedPlayers.size();
        p.sendMessage("§bYou completed the race in " + getColorByIndex(finishIndex) + placeStrings[finishIndex] + " place§b!");
        p.sendMessage("§bYou had a time of §n" + RaceUtilities.formatTimeString(elapsed) + getFinalTimeAheadString(p));
        finishedPlayers.add(p);
        this.endPlayerRace(p);
    }

    @Override
    protected String getSplitString(Player p, long elapsed) {
        int placement = this.raceStates.get(p).getPlacement();
        return " -- " + getColorByIndex(placement) + placeStrings[this.raceStates.get(p).getPlacement()] + getTimeToAheadString(p);
    }

    private String getTimeToAheadString(Player player) {
        RaceState state = this.raceStates.get(player);
        if (state.getPlacement() == 0) { return ""; }
        for (Player p : this.raceStates.keySet()) {
            if (player == p) { continue; }
            RaceState rs = this.raceStates.get(p);
            if (rs.getPlacement() == state.getPlacement() - 1 && rs.getCheckpointIndex() >= state.getCheckpointIndex()) {
                return "§6 "
                        + RaceUtilities.formatTimeString(state.getElapsed() - rs.getSplit(state.getCheckpointIndex() - 1))
                        + " behind " + p.getDisplayName();
            }
        }
        return "";
    }

    private String getFinalTimeAheadString(Player player) {
        RaceState state = this.raceStates.get(player);
        if (state.getPlacement() == 0) { return ""; }
        long highestEnd = 0;
        Player playerBehind = player;
        for (Player p : this.raceStates.keySet()) {
            if (player == p) { continue; }
            RaceState rs = this.raceStates.get(p);
            if (rs.getEndTime() >= highestEnd) {
                highestEnd = rs.getEndTime();
                playerBehind = p;
            }
        }
        if (highestEnd != 0) {
            return "§b which was "
                    + RaceUtilities.formatTimeString(state.getEndTime() - highestEnd)
                    + " behind " + playerBehind.getDisplayName();
        }
        return "";
    }

    protected String getColorByIndex(int index) {
        switch (index) {
            case 0:
                return "§e§l";
            case 1:
                return "§7§l";
            case 2:
                return "§6§l";
            default:
                return "§b";
        }
    }

    protected List<String> finalResults() {
        List<String> finalResults = new ArrayList<>();
        List<Player> processedPlayers = new ArrayList<>();
        finalResults.add("§b§lFinal results on §e§l" + track.getName());
        for (int i = 0; i < finishedPlayers.size(); i++) {
            Player p = finishedPlayers.get(i);
            finalResults.add(getColorByIndex(i) + placeStrings[i]
            + "§b: §e" + p.getDisplayName() + "§f -- §b" + RaceUtilities.formatTimeString(this.raceStates.get(p).getEndTime()));
            processedPlayers.add(p);
        }

        for (Player p : players) {
            if (!processedPlayers.contains(p)) {
               finalResults.add("§cDNF§b: §e" + p.getDisplayName());
            }
        }
        return finalResults;
    }

    @Override
    protected void endPlayerRace(Player player) {
        this.removePlayerFromRaceAndSendToLoc(player, track.getExit());
        for (RaceState rs : raceStates.values()) {
            if (rs.getCheckpointIndex() < track.getCheckpoints().size()) {
                return;
            }
        }
        finalResults().forEach((s -> players.forEach(p -> p.sendMessage(s))));
        players.clear();
        RaceManager.finishRace(track);
    }
}
