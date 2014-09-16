package com.marius.komgikk.rest;

import com.google.gson.Gson;
import com.marius.komgikk.service.UserService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/user")
public class UserApi {

    private UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getCurrentUser() {
        return new Gson().toJson(userService.getCurrentUser());
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
