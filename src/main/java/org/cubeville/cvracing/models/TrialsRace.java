package org.cubeville.cvracing.models;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.cubeville.cvracing.*;
import java.util.UUID;

public class TrialsRace extends Race {

    private Player player;
    private Score comparingTime;
    private Score personalBest;

    public TrialsRace(Track track, JavaPlugin plugin, Player player) {
        super(track, plugin, 1);
        this.player = player;
        this.personalBest = ScoreManager.getScore(player.getUniqueId(), track);
        this.comparingTime = determineComparingTime();
        this.startRace();
    }

    private Score determineComparingTime() {
        if (SelectedSplits.isUsingWR(player.getUniqueId())) {
            return ScoreManager.getWRScore(this.getTrack());
        }

        UUID selectedSplitPlayer = SelectedSplits.getSelectedSplitPlayer(player.getUniqueId());
        if (selectedSplitPlayer != null) {
            return ScoreManager.getScore(selectedSplitPlayer, this.getTrack());
        }
        return this.personalBest;
    }

    public void startRace() {
        raceStates.put(player, new RaceState(player));
        this.getTrack().setStatus(TrackStatus.IN_USE);
        this.setupPlayerOnTrack(player, this.getTrack().getTrialsSpawn());
        runCountdown(player, 3);
    }

    protected String getSplitString(Player p, long currentTime) {
        if (comparingTime != null) {
            long comparingSplit = comparingTime.getSplit(this.raceStates.get(p).getCheckpointIndex() - 1);
            String comparingName = "";
            if (!comparingTime.getPlayerUUID().equals(p.getUniqueId())) {
                comparingName = " -- " + comparingTime.getPlayerName();
            }
            if (comparingSplit > currentTime) {
                return " §6(§a-" + RaceUtilities.formatTimeString(comparingSplit - currentTime) + "§6)" + comparingName;
            } else if (comparingSplit < currentTime) {
                return " §6(§c+" + RaceUtilities.formatTimeString(currentTime - comparingSplit) + "§6)" + comparingName;
            } else {
                return " §6(§e00:00.00§6)" + comparingName;
            }
        }
        return "";
    }

    public void completeRace(Player p) {
        String pbString = " ";
        long elapsed = raceStates.get(p).getElapsed();
        if (personalBest == null || personalBest.getFinalTime() > elapsed) {
            String pbBy = "";
            if (personalBest != null) {
                pbBy = ", which was your personal best by " + RaceUtilities.formatTimeString(personalBest.getFinalTime() - elapsed);
            }
            p.sendMessage("§bYou achieved a time of " + RaceUtilities.formatTimeString(elapsed) + " on " + track.getName() + pbBy + "!");
            pbString = "§a§lNew Personal Best!";
            Score wr = ScoreManager.getWRScore(track);
            ScoreManager.setNewPB(p.getUniqueId(), track, elapsed, raceStates.get(p).getSplits());
            if (wr == null || elapsed < wr.getFinalTime()) {
                String broadcastString = "&b&l" + p.getName() + "&3 just got a new world record time of &b&l" + RaceUtilities.formatTimeString(elapsed) + "&3 on &b&l" + track.getName() + "&3!";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "runalias /announceboatswr " + broadcastString);
            } else {
                String broadcastString = "&d&l" + p.getName() + "&5 just got a new personal best time of &d&l" + RaceUtilities.formatTimeString(elapsed) + "&5, which put them at rank &d&l#" + ScoreManager.getScorePlacement(track, p.getUniqueId()) + "&5 on &d&l" + track.getName() + "&5!";
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "runalias /announceboatspb " + broadcastString);
            }
            if (ScoreManager.shouldRefreshLeaderboard(elapsed, track)) {
                track.loadLeaderboards();
            }
        } else {
            p.sendMessage("§bYou achieved a time of " + RaceUtilities.formatTimeString(elapsed) + " on " + track.getName() + ", which was " + RaceUtilities.formatTimeString(elapsed - personalBest.getFinalTime()) + " behind your personal best!");
        }
        p.sendTitle("§d§l" + RaceUtilities.formatTimeString(elapsed), pbString, 5, 90, 5);
        this.endPlayerRace(p);
    }

    protected void endPlayerRace(Player p) {
        RaceManager.removeRace(p);
        this.removePlayerFromRaceAndSendToLoc(p, track.getExit());
        RaceManager.finishRace(track);
    }
}
