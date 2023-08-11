package refactoring.controller;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refactoring.HttpRequest;
import refactoring.HttpResponse;
import webserver.WebServer;

import java.util.List;

public class ListUserController implements Controller{
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        if (!request.checkLogin()) {
            response.sendRedirect("/login.html");
            return;
        }
        List<User> users = WebServer.database.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=1>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("</tr>");
        }
        response.forwardBody(sb.toString());
    }
}
