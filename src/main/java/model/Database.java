package model;

import java.util.*;

public class Database {
    private static Map<String, User> users = new HashMap<>();

    public static User addUser(User user) {
        users.put(user.getUserId(), user);
        return user;
    }
    public static User findByUserId(String userId) {
        return users.get(userId);
    }
    public static Collection<User> findAll() {
        return users.values();
    }
}
