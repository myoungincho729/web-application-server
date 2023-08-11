package model;

import java.util.*;

public class UserRepository {
    Set<User> users = new HashSet<>();

    public User save(User user) {
        users.add(user);
        return user;
    }

    public boolean tryLogin(String userId, String password) {
        for (User user : users) {
            if (user.isCorrectInfo(userId, password)) {
                return true;
            }
        }
        return false;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void printUsers() {
        System.out.println("print Users : ");
        for (User user : users) {
            System.out.println(user);
        }
    }
}
