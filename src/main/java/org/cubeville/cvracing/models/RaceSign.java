package org.cubeville.cvracing.models;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.cubeville.cvracing.TrackStatus;

public class RaceSign {

	private Sign sign;
	private Track track;

	public RaceSign(Sign sign, Track track) {
		this.sign = sign;
		this.track = track;
		this.sign.setLine(1, track.getName());
		this.displayStatus(track.getStatus());
		this.displayQueue(track.getQueue().size());
	}

	public void displayQueue(int queueLength) {
		if (queueLength > 0) {
			this.sign.setLine(3, "§6§o" + queueLength + " in queue");
		} else {
			this.sign.setLine(3, "");
		}
		this.sign.update();
	}

	public void displayStatus(TrackStatus status) {
		switch (status) {
			case OPEN:
				this.sign.setLine(2, "§a§lOPEN");
				break;
			case IN_USE:
				this.sign.setLine(2, "§7§lIN USE");
				break;
			case CLOSED:
				this.sign.setLine(2, "§c§lCLOSED");
				break;
		}
		this.sign.update();
	}

	public Sign getSign() {
		return sign;
	}

	public void onRightClick(Player p) {
		this.track.onRightClick(p);
	}
}
