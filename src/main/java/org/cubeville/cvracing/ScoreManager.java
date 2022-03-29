package org.cubeville.cvracing;

import org.cubeville.cvracing.dbfiles.RacingDB;
import org.cubeville.cvracing.models.Score;
import org.cubeville.cvracing.models.Track;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ScoreManager {

	private static HashMap<String, TreeSet<Score>> scoreManager = new HashMap<>();
	private static RacingDB database;

	public static void importDataFromDatabase(RacingDB db) {
		HashMap<Integer, Score> importScoreManager = new HashMap<>();
		database = db;
		importScoresFromDB(db, importScoreManager);
		importSplitsFromDB(db, importScoreManager);
		for (Score score : importScoreManager.values()) {
			addScoreToManager(score);
		}
	}

	private static void importScoresFromDB(RacingDB db, HashMap<Integer, Score> importScoreManager) {
		try {
			ResultSet scoresSet = db.getAllScores();
			if (scoresSet == null) { return; }
			while (scoresSet.next()) {
				int scoreID = scoresSet.getInt("score_id");
				Track track = TrackManager.getTrack(scoresSet.getString("track_id"));
				UUID playerUUID = UUID.fromString(scoresSet.getString("uuid"));
				Score s = new Score(
					scoresSet.getLong("time"),
					track,
					playerUUID,
					scoresSet.getLong("timestamp")
					);
				importScoreManager.put(scoreID, s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void importSplitsFromDB(RacingDB db, HashMap<Integer, Score> importScoreManager) {
		try {
			ResultSet splitSet = db.getAllSplits();
			if (splitSet == null) { return; }
			while (splitSet.next()) {
				Score score = importScoreManager.get(splitSet.getInt("score_id"));
				if (score == null) {
					db.deleteSplitsAtScore(splitSet.getInt("score_id"));
					break;
				}
				score.addSplit(splitSet.getInt("cp_id"), splitSet.getLong("time"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static TreeSet<Score> getTrackScores(Track track) {
		return scoreManager.get(track.getName());
	}

	public static Score getScore(UUID uuid, Track track) {
		TreeSet<Score> scoreList = scoreManager.get(track.getName());
		if (scoreList != null) {
			for (Score score : scoreList) {
				if (score.getPlayerUUID().equals(uuid)) {
					return score;
				}
			}
		}
		return null;
	}

	public static Score getWRScore(Track track) {
		if (getTrackScores(track) != null) {
			return getTrackScores(track).first();
		};
		return null;
	}

	public static Integer getScorePlacement(Track track, UUID uuid) {
		if (getTrackScores(track) == null) {
			return null;
		}
		List<Score> sortedTimes = new ArrayList<>(getTrackScores(track));
		for (int i = 0; i < sortedTimes.size(); i++) {
			if (sortedTimes.get(i).getPlayerUUID().equals(uuid)) {
				return i + 1;
			}
		}
		return null;
	}

	public static Score addScore(UUID uuid, Track track, long finalTime, HashMap<Integer, Long> splits) {
		Score s = new Score(finalTime, track, uuid, System.currentTimeMillis());
		s.setSplits(splits);
		addScoreToManager(s);
		return s;
	}

	public static boolean shouldRefreshLeaderboard(long finalTime, Track track) {
		if (getTrackScores(track) == null) {
			return true;
		}
		List<Score> scores = new ArrayList<>(getTrackScores(track));
		if (scores.size() <= 10) { return true; }
		return scores.get(9).getFinalTime() >= finalTime;
	}

	public static void addScoreToManager(Score score) {
		TreeSet<Score> oldList = scoreManager.get(score.getTrack().getName());
		if (oldList == null) {
			oldList = new TreeSet<>(Comparator.comparingLong(Score::getFinalTime).thenComparingLong(Score::getTimestamp));
		}
		oldList.add(score);
		scoreManager.put(score.getTrack().getName(), oldList);
	}

	public static void setNewPB(UUID uuid, Track track, long finalTime, HashMap<Integer, Long> splits) {
		Score s = getScore(uuid, track);
		Score newScore = addScore(uuid, track, finalTime, splits);
		if (s == null) {
			database.addScore(newScore);
		} else {
			// update the score that is currently in the db
			scoreManager.get(track.getName()).remove(s);
			database.updateScore(newScore);
		}
	}

	public static void deleteScore(Score deleting) {
		TreeSet<Score> scoreList = scoreManager.get(deleting.getTrack().getName());
		if (scoreList.size() <= 1) {
			deleteAllScores(deleting.getTrack());
		} else {
			database.deleteScore(deleting);
		}
		scoreList.remove(deleting);
	}

	public static void deleteAllScores(Track track) {
		scoreManager.remove(track.getName());
		database.deleteTrackSplits(track);
		database.deleteTrackScores(track);
	}

	public static void deletePlayerScores(UUID uuid) {
		for (String trackName : scoreManager.keySet()) {
			TreeSet<Score> scores = scoreManager.get(trackName);
			Score removingScore = null;
			for (Score score : scores) {
				if (score.getPlayerUUID().equals(uuid)) {
					removingScore = score;
					break;
				}
			}
			if (removingScore != null) {
				scores.remove(removingScore);
				TrackManager.getTrack(trackName).loadLeaderboards();
			}
		}
		database.deletePlayerScores(uuid);
	}
}
