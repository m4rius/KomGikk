package com.marius.komgikk.servlet;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by marius on 01.09.14.
 */
public class RestrictedAreaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getWriter().println("Hello, this is a restricted area \n\n");

        UserService userService = UserServiceFactory.getUserService();
        User currentUser = userService.getCurrentUser();

        resp.getWriter().println(String.format("Nick: %s", currentUser.getNickname()));
        resp.getWriter().println(String.format("Id: %s", currentUser.getUserId()));
        resp.getWriter().println(String.format("email: %s", currentUser.getEmail()));

    }
}
