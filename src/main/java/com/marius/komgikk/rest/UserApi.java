package com.marius.komgikk.rest;

import com.google.gson.Gson;
import com.marius.komgikk.domain.Activity;
import com.marius.komgikk.domain.JsonKomGikkUser;
import com.marius.komgikk.domain.KomGikkUser;
import com.marius.komgikk.service.UserService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserApi {

    private UserService userService = new UserService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getCurrentUser() {
        KomGikkUser currentUser = userService.getCurrentUser();
        if (currentUser.createdNow) {
            Activity.storeDefaults(currentUser);
        }
        return new Gson().toJson(currentUser.forJson());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void store(String json) {
        JsonKomGikkUser jsonKomGikkUser  = new Gson().fromJson(json, JsonKomGikkUser.class);

        KomGikkUser komGikkUser = KomGikkUser.get(jsonKomGikkUser.username);
        komGikkUser.setName(jsonKomGikkUser.name);
        komGikkUser.store();
    }
}
