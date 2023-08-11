package model;

import java.util.Map;

public class User {
	private String userId;
	private String password;
	private String name;
	private String email;
	
	public User(String userId, String password, String name, String email) {
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.email = email;
	}
	public static User from(Map<String, String> querymap) {
		return new User(
				querymap.get("userId"),
				querymap.get("password"),
				querymap.get("name"),
				querymap.get("email")
		);
	}
	public String getUserId() {
		return userId;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", password=" + password + ", name="
				+ name + ", email=" + email + "]";
	}

	public boolean isCorrectInfo(String userId, String password) {
		return this.userId.equals(userId) && this.password.equals(password);
	}
}
