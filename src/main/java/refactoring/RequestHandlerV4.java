package refactoring;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.WebServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

//HttpRequest + HttpResponse + 요청URL 분기처리 리팩토링
public class RequestHandlerV4 extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandlerV4.class);

    private Socket connection;

    public RequestHandlerV4(Socket connectionSocket) {
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

            if ("/user/create".equals(path)) {
                createUser(httpRequest, httpResponse);
            }
            else if ("/user/login".equals(path)) {
                login(httpRequest, httpResponse);
            }
            else if ("/user/list".equals(path)) {
                listUser(httpRequest, httpResponse);
            }
            else {
                httpResponse.forward(path);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void listUser(HttpRequest httpRequest, HttpResponse httpResponse) {
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

    private void login(HttpRequest httpRequest, HttpResponse httpResponse) {
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

    private void createUser(HttpRequest httpRequest, HttpResponse httpResponse) {
        User user = User.from(httpRequest.getParameters());
        log.debug("user : {}", user);
        WebServer.database.addUser(user);
        httpResponse.sendRedirect("/index.html");
    }
}

