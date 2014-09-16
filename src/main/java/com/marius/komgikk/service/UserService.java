package com.marius.komgikk.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.marius.komgikk.domain.KomGikkUser;

import java.util.Map;

public class UserService {

    public KomGikkUser store(String json) {
        Map map = new Gson().fromJson(json, Map.class);

        return new KomGikkUser((Map) map.get("params")).store();

    }

    public KomGikkUser getCurrentUser() {
        com.google.appengine.api.users.UserService userService = UserServiceFactory.getUserService();
        User googleUser = userService.getCurrentUser();

        if (googleUser == null) {
            //todo return not autorized http feilmelding eller noe sånt
            throw new RuntimeException("ikke pålogget");
        }


        KomGikkUser user = KomGikkUser.get(googleUser.getUserId());
        if (user == null) {
            user = new KomGikkUser(googleUser.getUserId(), googleUser.getEmail(), googleUser.getNickname());
            user.store();
        }
        return user;
    }
}
