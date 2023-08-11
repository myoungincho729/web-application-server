package refactoring.controller;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refactoring.HttpRequest;
import refactoring.HttpResponse;
import webserver.WebServer;

public class CreateUserController implements Controller{
    private static final Logger log = LoggerFactory.getLogger(CreateUserController.class);
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        User user = User.from(request.getParameters());
        log.debug("user : {}", user);
        WebServer.database.addUser(user);
        response.sendRedirect("/index.html");
    }
}
