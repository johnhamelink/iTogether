package com.farmgeek.iTogether.models;

import java.util.Hashtable;

/**
 * Created by john on 25/04/2014.
 */
public class UserManager {

    protected static Hashtable<String, User> users = new Hashtable<String, User>();

    public static void push(User user) {
        UserManager.users.put(user.get_uuid(), user);
    }

    public static User get(String uuid) {
        return UserManager.users.get(uuid);
    }

    public static User first() {
        return UserManager.users.elements().nextElement();
    }

    public static boolean has(String uuid) {
        return UserManager.users.containsKey(uuid);
    }

    public static Hashtable<String, User> getUsers() {
        return users;
    }
}
