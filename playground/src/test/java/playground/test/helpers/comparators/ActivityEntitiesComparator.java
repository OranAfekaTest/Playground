package playground.test.helpers.comparators;

import java.util.Comparator;

import playground.logic.entity.ActivityEntity;

public class ActivityEntitiesComparator implements Comparator<ActivityEntity>{

	@Override
	public int compare(ActivityEntity a1, ActivityEntity a2) {
		int rv = a1.getPlayground().compareTo(a2.getPlayground());
		if (rv == 0) {
			rv = a1.getElementPlayground().compareTo(a2.getElementPlayground());
			if (rv == 0) {
				rv = a1.getElementId().compareTo(a2.getElementId());
				if (rv == 0) {
					rv = a1.getType().compareTo(a2.getType());
					if (rv == 0) {
						rv = a1.getPlayerPlayground().compareTo(a2.getPlayerPlayground());
						if (rv == 0) {
							rv = a1.getPlayerEmail().compareTo(a2.getPlayerEmail());
							if (rv == 0) {
								rv = a1.getId().compareTo(a2.getId());
								if (rv == 0) {
									rv = a1.getCreationDate().compareTo(a2.getCreationDate());
								}
							}
						}
					}
				}
			}
		}
		return rv;
	}
	public int compareForCreationOfNewActivity(ActivityEntity a1, ActivityEntity a2) {
		int rv = a1.getPlayground().compareTo(a2.getPlayground());
		if (rv == 0) {
			rv = a1.getElementPlayground().compareTo(a2.getElementPlayground());
			if (rv == 0) {
				rv = a1.getElementId().compareTo(a2.getElementId());
				if (rv == 0) {
					rv = a1.getType().compareTo(a2.getType());
					if (rv == 0) {
						rv = a1.getPlayerPlayground().compareTo(a2.getPlayerPlayground());
						if (rv == 0) {
							rv = a1.getPlayerEmail().compareTo(a2.getPlayerEmail());
							if (rv == 0) {
								if (a1.getId() != null) {
									rv = 0;
									if (a1.getCreationDate() != null) {
										rv = 0;
									}
									else {
										rv = 1;
									}
								}
								else {
									rv = 1;
								}
							}
						}
					}
				}
			}
		}
		return rv;
	}

}
