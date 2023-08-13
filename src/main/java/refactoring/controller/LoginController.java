package refactoring.controller;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refactoring.HttpRequest;
import refactoring.HttpResponse;
import webserver.WebServer;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        if (userId == null || password == null) {
            response.sendRedirect("/login-failed.html");
        }
        User user = Database.findByUserId(userId);
        if (user != null && user.isCorrectInfo(userId, password)) {
            response.addHeader("Set-Cookie", "logined=true");
            response.sendRedirect("/index.html");
        } else {
            response.sendRedirect("/login-failed.html");
        }
    }
}
