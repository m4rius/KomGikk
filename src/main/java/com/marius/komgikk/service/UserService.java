package com.marius.komgikk.service;

import com.google.gson.Gson;
import com.marius.komgikk.domain.User;

import java.util.Map;

/**
 * Created by marius on 31.08.14.
 */
public class UserService {

    /**
     *
     * Forventer json:
     *
     * {"command":"store", "entity":"user", "params": {"username":"user1", "name":"nils" , "password":"pass1", "email":"nils.nilsen@gmail.com"}}
     *
     * @param json
     * @return
     */

    public User store(String json) {
        Map map = new Gson().fromJson(json, Map.class);

        return new User((Map) map.get("params")).store();

    }
}
