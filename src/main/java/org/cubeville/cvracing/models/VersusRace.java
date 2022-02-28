package org.cubeville.cvracing.models;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvracing.RaceManager;
import org.cubeville.cvracing.TrackStatus;

import java.util.ArrayList;
import java.util.List;

public class VersusRace extends Race {
    List<Player> players = new ArrayList<>();
    List<Player> finishedPlayers = new ArrayList<>();
    private int lobbyTimeout;
    private int lobbyCountdown = 0;
    private int countdownValue;
    public int maxPlayers = 4;
    // I'm stopping at 30th, I do not like places lol
    private final String[] placeStrings = {
            "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th",
            "10th", "11th", "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th",
            "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th", "30th"
    };

    final int LOBBY_TIMEOUT_MINUTES = 1;

    public VersusRace(Track track, JavaPlugin plugin) {
        super(track, plugin);
        startLobbyTimeout();
    }

    public void addPlayer(Player p) {
        players.add(p);
        players.forEach(player -> player.sendMessage(p.getDisplayName() + " has joined the race."));

        System.out.println("lc is: " + lobbyCountdown);
        if (playerSize() > 1 && lobbyCountdown == 0) {
            setLobbyCountdown(15);
        }

        if (playerSize() >= maxPlayers) {
            players.forEach(player -> player.sendMessage("This lobby is now full!"));
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
                p.sendMessage("The queue for this race has timed out.");
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
                    player.sendMessage("Race starting in " + countdownValue + "...");
                });
            } else if (countdownValue % 15 == 0) {
                players.forEach(player -> {
                    player.playSound(player.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 2F, 1F);
                    player.sendMessage("Race starting in " + countdownValue + " seconds");
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
        System.out.println(players + " starting versus race");
        for (Player player : players) {
            System.out.println(player.getDisplayName() + " starting versus race");
            this.setupPlayerOnTrack(player, this.getTrack().getSpawn());
            runCountdown(player, 3);
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
        p.sendMessage("You completed the race in " + placeStrings[finishedPlayers.size()] + " place");
        finishedPlayers.add(p);
        this.endPlayerRace(p);
    }

    @Override
    protected String getSplitString(Player p, long elapsed) {
        return " -- " + placeStrings[this.raceStates.get(p).getPlacement()] + " place";
    }

    @Override
    protected void endPlayerRace(Player p) {
        this.removePlayerFromRaceAndSendToLoc(p, track.getExit());
        if (this.raceStates.size() == 0) {
            RaceManager.finishRace(track);
        }
    }
}
