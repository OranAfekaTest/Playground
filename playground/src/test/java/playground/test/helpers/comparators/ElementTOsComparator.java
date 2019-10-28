package playground.test.helpers.comparators;

import java.util.Comparator;

import playground.layout.io.ElementTO;

public class ElementTOsComparator implements Comparator<ElementTO>{

	@Override
	public int compare(ElementTO e1, ElementTO e2) {
		int rv = e1.getPlayground().compareTo(e2.getPlayground());
		if (rv == 0) {
			rv = e1.getLocation().compareTo(e2.getLocation());
			if (rv == 0) {
				rv = e1.getName().compareTo(e2.getName());
				if (rv == 0) {
					rv = e1.getExpirationDate().compareTo(e2.getExpirationDate());
					if (rv == 0) {
						rv = e1.getType().compareTo(e2.getType());
						if (rv == 0) {
							rv = e1.getCreatorPlayground().compareTo(e2.getCreatorPlayground());
							if (rv == 0) {
								rv = e1.getCreatorEmail().compareTo(e2.getCreatorEmail());
								if (rv == 0) {
									rv = e1.getCreationDate().compareTo(e2.getCreationDate());
									if (rv == 0) {
										rv = e1.getId().compareTo(e2.getId());
									}
								}
							}
						}
					}
				}
			}
		}
		return rv;
	}
	
	public int compareForCreationOfNewElement(ElementTO e1, ElementTO e2) {
		int rv = e1.getPlayground().compareTo(e2.getPlayground());
		if (rv == 0) {
			rv = e1.getLocation().compareTo(e2.getLocation());
			if (rv == 0) {
				rv = e1.getName().compareTo(e2.getName());
				if (rv == 0) {
					rv = e1.getExpirationDate().compareTo(e2.getExpirationDate());
					if (rv == 0) {
						rv = e1.getType().compareTo(e2.getType());
						if (rv == 0) {
							rv = e1.getCreatorPlayground().compareTo(e2.getCreatorPlayground());
							if (rv == 0) {
								rv = e1.getCreatorEmail().compareTo(e2.getCreatorEmail());
								if (rv == 0) {
									if (e1.getCreationDate() != null && !e1.getCreationDate().equals(e2.getCreationDate())) {
										rv = 0;
									}
									else {
										rv = 1;
									}
									if (rv == 0) {
										if (e1.getId() != null) {
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
			}
		}
		return rv;
	}

}
