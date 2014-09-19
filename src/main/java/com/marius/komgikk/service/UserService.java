package com.marius.komgikk.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.marius.komgikk.domain.KomGikkUser;

public class UserService {

    public KomGikkUser getCurrentUser() {
        com.google.appengine.api.users.UserService userService = UserServiceFactory.getUserService();
        User googleUser = userService.getCurrentUser();

        if (googleUser == null) {
            //todo return not autorized http feilmelding eller noe sånt
            throw new RuntimeException("ikke pålogget");
        }


        KomGikkUser user = KomGikkUser.get(googleUser.getUserId());
        if (user == null) {
            user = new KomGikkUser(googleUser.getUserId(), googleUser.getEmail());
            user.store();
        }
        return user;
    }
}
