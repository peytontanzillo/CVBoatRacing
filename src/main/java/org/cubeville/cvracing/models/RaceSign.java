package org.cubeville.cvracing.models;

import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.cubeville.cvracing.RaceSignType;
import org.cubeville.cvracing.TrackStatus;

public class RaceSign {

	private final Sign sign;
	private final Track track;
	private RaceSignType type;
	private int laps = 1;

	public RaceSign(Sign sign, Track track, RaceSignType type) {
		this.sign = sign;
		this.track = track;
		this.sign.getSide(Side.FRONT).setLine(1, track.getName());
		this.setType(type);
		this.displayQueue();
		this.displayStatus(track.getStatus());
	}

	public void displayType() {
		if (track.getHostedRace() != null && type != RaceSignType.ERROR && type != RaceSignType.EXIT) {
			this.sign.getSide(Side.FRONT).setLine(0, "§e§l[HOSTING]");
			this.sign.update();
			return;
		}
		String typeText = "";
		switch (type) {
			case TRIALS:
				typeText += "§b";
				break;
			case VERSUS:
				typeText += "§d";
				break;
			case SPECTATE:
				typeText += "§7";
				break;
			case EXIT:
			case ERROR:
				typeText += "§c";
				break;
		}
		typeText += "§l[";
		typeText += type;
		typeText += "]";
		this.sign.getSide(Side.FRONT).setLine(0, typeText);
		this.sign.update();
	}

	public void displayQueue() {
		if (track.getHostedRace() != null) {
			this.sign.getSide(Side.FRONT).setLine(3, "");
			this.sign.update();
			return;
		}
		switch (type) {
			case TRIALS:
				if (track.getQueue().size() > 0) {
					this.sign.getSide(Side.FRONT).setLine(3, "§6§o" + track.getQueue().size() + " in queue");
				} else {
					this.sign.getSide(Side.FRONT).setLine(3, "");
				}
				break;
			case VERSUS:
				int playerFill = 0;
				if (track.getVersusRace() != null) {
					playerFill = track.getVersusRace().playerSize();
				}
				this.sign.getSide(Side.FRONT).setLine(3, playerFill + " / " + track.getVersusSpawns().size());
				break;
			default:
				this.sign.getSide(Side.FRONT).setLine(3, "");
				break;
		}
		this.sign.update();
	}

	public void displayStatus(TrackStatus status) {
		boolean displayStatusAlways = type == RaceSignType.TRIALS || type == RaceSignType.VERSUS;
		boolean shouldOverrideStatusHide = status == TrackStatus.HOSTING && type == RaceSignType.SPECTATE;
		if (!displayStatusAlways && !shouldOverrideStatusHide) {
			this.sign.getSide(Side.FRONT).setLine(2, "");
		} else {
			switch (status) {
				case OPEN:
					this.sign.getSide(Side.FRONT).setLine(2, "§a§lOPEN");
					break;
				case IN_USE:
					this.sign.getSide(Side.FRONT).setLine(2, "§7§lIN USE");
					break;
				case CLOSED:
					this.sign.getSide(Side.FRONT).setLine(2, "§c§lCLOSED");
					break;
				case IN_LOBBY:
					this.sign.getSide(Side.FRONT).setLine(2, "§e§lIN LOBBY");
					break;
				case HOSTING:
					this.sign.getSide(Side.FRONT).setLine(2, "§b§lHOSTING");
					break;
			}
		}
		this.sign.update();
	}

	public void setType(RaceSignType type) {
		this.type = type;
		this.displayType();
		this.displayStatus(track.getStatus());
	}

	public Sign getSign() {
		return sign;
	}

	public Track getTrack() {
		return track;
	}

	public void onRightClick(Player p) {
		this.track.onRightClick(p, type, laps);
	}

	public void setLaps(int laps) {
		this.laps = laps;
	}
}
