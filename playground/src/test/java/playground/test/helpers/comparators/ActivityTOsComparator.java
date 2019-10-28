package playground.test.helpers.comparators;

import java.util.Comparator;

import playground.layout.io.ActivityTO;

public class ActivityTOsComparator implements Comparator<ActivityTO>{

	@Override
	public int compare(ActivityTO a1, ActivityTO a2) {
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
							}
						}
					}
				}
			}
		}
		return rv;
	}
	public int compareForCreationOfNewActivity(ActivityTO a1, ActivityTO a2) {
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
