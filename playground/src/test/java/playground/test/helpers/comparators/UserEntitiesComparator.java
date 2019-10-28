package playground.test.helpers.comparators;

import java.util.Comparator;

import playground.logic.entity.UserEntity;

public class UserEntitiesComparator implements Comparator<UserEntity> {

	@Override
	public int compare(UserEntity u1, UserEntity u2) {
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
							rv = u1.getPoints().compareTo(u2.getPoints());
							if (rv == 0 ) {
								rv = u1.getCode().compareTo(u2.getCode());
							}
						}
					}
				}
			}
		}
		return rv;
	}

}
