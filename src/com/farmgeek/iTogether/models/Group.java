package com.farmgeek.iTogether.models;

import java.util.Hashtable;

/**
 * Created by john on 25/04/2014.
 */
public class Group {

    protected Hashtable<String, User> _users = new Hashtable<String, User>();

    public Hashtable<String, User> get_users() {
        return this._users;
    }

    public User get_user(String uuid) {
        return this._users.get(uuid);
    }

    public User remove_user(String uuid) {
        return this._users.remove(uuid);
    }

    public void clear_users() {
        this._users.clear();
    }

    public void pushUser(String uuid, User user) {
        this._users.put(uuid, user);
    }


}
