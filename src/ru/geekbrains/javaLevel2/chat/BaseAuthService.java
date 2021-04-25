package ru.geekbrains.javaLevel2.chat;

import java.util.HashMap;
import java.util.Map;

public class BaseAuthService implements AuthService{
    Map<String, User> users;
    @Override
    public void start() {
        users = new HashMap<>();
        users.put("log1", new User("log1", "pass1", "nick1"));
        users.put("log2", new User("log2", "pass2", "nick2"));
        users.put("log3", new User("log3", "pass3", "nick3"));
    }

    @Override
    public String getNickByLoginPass(String login, String password) {
        User user = users.get(login);
        if (user != null && user.getPassword().equals(password)) {
            return user.getNick();
        }
        return null;
    }

    @Override
    public void stop() {
        System.out.println("Service stopped");
    }
}
