package playground.logic.entity;

import java.io.Serializable;

public class UserId implements Serializable {
	
	/** Class Constants */
	private static final long serialVersionUID = -4859662238577281527L;
	
	/** Class Attributes */
	private String email;
	private String playground;
	
	/** Class Constructors */
	public UserId() {
	}
	
	public UserId(String email, String playground) {
		this.email = email;
		this.playground = playground;
	}
	
	/** Class Getters & Setters */
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPlayground() {
		return playground;
	}
	
	public void setPlayground(String playground) {
		this.playground = playground;
	}
	
	/** Class Methods */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((playground == null) ? 0 : playground.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserId other = (UserId) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (playground == null) {
			if (other.playground != null)
				return false;
		} else if (!playground.equals(other.playground))
			return false;
		return true;
	}
}
