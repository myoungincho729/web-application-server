package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    Map<String, User> users = new HashMap<>();

    public User addUser(User user) {
        users.put(user.getUserId(), user);
        return user;
    }
    public User findByUserId(String userId) {
        return users.get(userId);
    }
    public List<User> findAll() {
        return (List<User>) users.values();
    }
}
