package com.marius.komgikk.rest;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.marius.komgikk.domain.KomGikkUser;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserApi {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getCurrentUser() {
        UserService userService = UserServiceFactory.getUserService();
        User googleUser = userService.getCurrentUser();

        if (googleUser == null) {
            //todo return not autorized http feilmelding eller noe sånt
            throw new RuntimeException("ikke pålogget");
        }


        KomGikkUser user = KomGikkUser.get(googleUser.getUserId());
        if (user == null) {
            user = new KomGikkUser(googleUser.getUserId(), googleUser.getEmail(), googleUser.getNickname());
            //user.store();
        }

        return new Gson().toJson(user);
    }

//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public String roudtrip(String s) {
//        Person p = new Gson().fromJson(s, Person.class);
//
//        return new Gson().toJson(p);
//    }
}
