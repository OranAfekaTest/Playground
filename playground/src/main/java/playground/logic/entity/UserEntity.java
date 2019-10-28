package playground.logic.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(UserId.class)
@Table(name = "USERS")
public class UserEntity {
	
	/** Class Attributes */
	private String email;
	private String playground;
	private String username;
	private String avatar;
	private String role;
	private Long points;
	private String code; // For verification purposes. Empty <-> verified, !Empty <-> !verified
	private boolean isManager; // For manager that want to be player sometimes

	/** Class Constructors */
	public UserEntity() {
	}
	
	public UserEntity(String email, String playground, String username, String avatar, String role, Long points,
			String code) {
		setEmail(email);
		setPlayground(playground);
		setUsername(username);
		setAvatar(avatar);
		setRole(role);
		setPoints(points);
		setCode(code);
		if(role != null && role.equals("manager"))
			setIsManager(true);
		else
			setIsManager(false);
	}
	
	/** Class Getters & Setters */
	@Id
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@Id
	public String getPlayground() {
		return playground;
	}
	
	public void setPlayground(String playground) {
		this.playground = playground;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public void setIsManager(boolean isManager) {
		this.isManager = isManager;
	}
	
	public boolean getIsManager() {
		return isManager;
	}

	/** Class Methods */
	@Override
	public String toString() {
		return "UserEntity [email=" + email + ", playground=" + playground + ", username=" + username + ", avatar="
				+ avatar + ", role=" + role + ", points=" + points + ", code=" + code + "]";
	}
}
