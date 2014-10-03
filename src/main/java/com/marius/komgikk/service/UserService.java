package com.marius.komgikk.service;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.marius.komgikk.domain.KomGikkUser;

import java.util.logging.Logger;

public class UserService {

    public static final Logger LOG = Logger.getLogger(User.class.getName());

    /*
     * TODO Her er det trådproblemer. Samtidige tråder kan kalle denne. Måtte fjerne Activity.storeDefault. Da den ble kalt 4 ganger
     * Av en eller annen grunn blir det bare opprettet 1 bruker. Men det burde nok flyttes ut for sikkerhets skyld
     * Oppdatering: blir faktisk lagret alle gangene, men fordi userId brukes om key, så blir det lagres oppå hverandre
     */
    public KomGikkUser getCurrentUser() {
        com.google.appengine.api.users.UserService userService = UserServiceFactory.getUserService();
        User googleUser = userService.getCurrentUser();

        if (googleUser == null) {
            //todo return not autorized http feilmelding eller noe sånt
            throw new RuntimeException("ikke pålogget");
        }


        KomGikkUser user = KomGikkUser.get(googleUser.getUserId());
        if (user == null) {
            LOG.info(String.format("New user: %s - %s. Storing user and default activities",
                    googleUser.getEmail(),
                    googleUser.getUserId()));
            user = new KomGikkUser(googleUser.getUserId(), googleUser.getEmail());

            user.createdNow = true;
            user.store();
        }

        return user;
    }
}
