package refactoring.controller;

import model.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refactoring.HttpRequest;
import refactoring.HttpResponse;
import webserver.WebServer;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ListUserController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (!request.checkLogin()) {
            response.sendRedirect("/login.html");
            return;
        }
        Collection<User> users = Database.findAll();
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
