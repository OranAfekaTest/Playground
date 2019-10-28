package playground.layout.io;

import playground.logic.entity.UserEntity;

public class UserTO {
	
	/** Class Attributes */
	private String email;
	private String playground;
	private String username;
	private String avatar;
	private String role;
	private Long points;

	/** Class Constructors */
	public UserTO() {
	}
	
	public UserTO(String email, String playground, String username, String avatar, String role, Long points) {
		setEmail(email);
		setPlayground(playground);
		setUsername(username);
		setAvatar(avatar);
		setRole(role);
		setPoints(points);
	}

	public UserTO(UserEntity entity) {
		this(entity.getEmail(), entity.getPlayground(), entity.getUsername(), entity.getAvatar(), entity.getRole(), entity.getPoints());
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

	/** Class Methods */
	public UserEntity toEntity() {
		return new UserEntity(email, playground, username, avatar, role, points, "");
	}

	@Override
	public String toString() {
		return "UserTO [email=" + email + ", playground=" + playground + ", username=" + username + ", avatar=" + avatar
				+ ", role=" + role + ", points=" + points + "]";
	}
}
