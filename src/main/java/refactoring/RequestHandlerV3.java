package refactoring;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.WebServer;

//HttpRequest + HttpResponse 리팩토링
public class RequestHandlerV3 extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandlerV3.class);

    private Socket connection;

    public RequestHandlerV3(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            // request, response 객체 생성
            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);
            String path = httpRequest.getPath();


            if (httpRequest.getPath().equals("/favicon.ico")) {
                return;
            }
            if ("/user/create".equals(path)) {
                User user = User.from(httpRequest.getParameters());
                log.debug("user : {}", user);
                WebServer.database.addUser(user);
                httpResponse.sendRedirect("/index.html");
            }
            else if ("/user/login".equals(path)) {
                String userId = httpRequest.getParameter("userId");
                String password = httpRequest.getParameter("password");
                if (userId == null || password == null) {
                    httpResponse.sendRedirect("/login-failed.html");
                }
                User user = WebServer.database.findByUserId(userId);
                if (user != null && user.isCorrectInfo(userId, password)) {
                    httpResponse.addHeader("Set-Cookie", "logined=true");
                    httpResponse.sendRedirect("/index.html");
                } else {
                    httpResponse.sendRedirect("/login-failed.html");
                }
            }
            else if ("/user/list".equals(path)) {
                if (!httpRequest.checkLogin()) {
                    httpResponse.sendRedirect("/login.html");
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
                httpResponse.forwardBody(sb.toString());
            }
            else {
                httpResponse.forward(path);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

