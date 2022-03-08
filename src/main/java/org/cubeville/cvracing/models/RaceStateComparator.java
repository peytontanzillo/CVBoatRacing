package org.cubeville.cvracing.models;

import java.util.Comparator;

public class RaceStateComparator implements Comparator<RaceState> {

	int checkpointSize;

	public RaceStateComparator(int checkpointSize) {
		this.checkpointSize = checkpointSize;
	}

    @Override
    public int compare(RaceState rsA, RaceState rsB) {

		// If one has a higher cp index, sort by that value first
		if (rsA.getLapIndex() > rsB.getLapIndex()) {
			return -1;
		} else if (rsA.getLapIndex() < rsB.getLapIndex()) {
			return 1;
		}

		// If one has a higher cp index, sort by that value first
		if (rsA.getCheckpointIndex() > rsB.getCheckpointIndex()) {
			return -1;
		} else if (rsA.getCheckpointIndex() < rsB.getCheckpointIndex()) {
			return 1;
		}

		int splitIndex = (rsA.getLapIndex() * checkpointSize) + rsA.getCheckpointIndex() - 1;
		if (rsA.getEndTime() != 0 || rsB.getEndTime() != 0) {
			if (rsA.getEndTime() > rsB.getEndTime()) {
				return 1;
			}
			return -1;
		}

		// If neither of them have hit any splits, just sort randomly idc
		if (rsA.getSplits().size() == 0 || rsB.getSplits().size() == 0) {
			return 1;
		}

		// Sort by the previous CP split
		if (rsA.getSplit(splitIndex) > rsB.getSplit(splitIndex)) {
			return 1;
		}
		return -1;
    }
}
