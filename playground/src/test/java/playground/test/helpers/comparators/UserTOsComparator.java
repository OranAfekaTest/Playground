package playground.test.helpers.comparators;

import java.util.Comparator;

import playground.layout.io.UserTO;

public class UserTOsComparator implements Comparator<UserTO>{

	@Override
	public int compare(UserTO u1, UserTO u2) {
		int rv = u1.getEmail().compareTo(u2.getEmail());
		if (rv == 0) {
			rv = u1.getPlayground().compareTo(u2.getPlayground());
			if (rv == 0) {
				rv = u1.getUsername().compareTo(u2.getUsername());
				if (rv == 0) {
					rv = u1.getAvatar().compareTo(u2.getAvatar());
					if (rv == 0) {
						rv = u1.getRole().compareTo(u2.getRole());
						if (rv == 0) {
							rv = new Long(u1.getPoints()).compareTo(u2.getPoints());
						}
					}
				}
			}
		}
		return rv;
	}

}
