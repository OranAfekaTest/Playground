package playground.layout.io;

public class NewUserForm {
	
	/** Class Attributes */
	private String email;
	private String username;
	private String avatar;
	private String role;
	
	/** Class Constructors */
	public NewUserForm(){
	}
	
	public NewUserForm(String email, String username, String avatar, String role){
		super();
		this.email = email;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
	}

	/** Class Getters & Setters */
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
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
	
	/** Class Methods */
	@Override
	public String toString() {
		return "NewUserForm [email=" + email + ", username=" + username
				+ ", avatar=" + avatar + ", role=" + role + "]";
	}
}
