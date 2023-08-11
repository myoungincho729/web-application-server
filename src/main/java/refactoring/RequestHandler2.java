package refactoring;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import util.IOUtils;
import webserver.HttpHeader;
import webserver.WebServer;

public class RequestHandler2 extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler2.class);

    private Socket connection;

    public RequestHandler2(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            DataOutputStream dos = new DataOutputStream(out);

            // 헤더와 라인 구별
            HttpRequest httpRequest = new HttpRequest(in);

            // body 구현
            BufferedReader brf = null;
            String content;
            byte[] body = new byte[0];

            if (httpRequest.getPath().equals("/favicon.ico")) {
                return;
            }
            else if (httpRequest.getHeader("Accept").startsWith("text/css") && httpRequest.getPath().startsWith("/stylesheets")) {
                body = Files.readAllBytes(Path.of("./webapp" + httpRequest.getPath()));
                response200CSS(dos, body.length);
                responseBody(dos, body);
                return;
            }
            else if (httpRequest.getPath().equals("/index.html")) {
                body = Files.readAllBytes(Path.of("./webapp/index.html"));
            }
            else if (httpRequest.getPath().equals("/form.html")) {
                body = Files.readAllBytes(Path.of("./webapp/form.html"));
            }
            else if (httpRequest.getPath().equals("/login.html")) {
                body = Files.readAllBytes(Path.of("./webapp/login.html"));
            }
            else if (httpRequest.getMethod().equals(HttpMethod.GET) && httpRequest.getPath().equals("/login")) {
                if (WebServer.userRepository.tryLogin(
                        httpRequest.getParameter("userId"),
                        httpRequest.getParameter("password"))) {
                    response302HeaderWithLogin(dos, "/index.html", true);
                    return;
                } else {
                    response302HeaderWithLogin(dos, "/user/login_failed.html", false);
                    return;
                }
            }
            else if (httpRequest.getMethod().equals(HttpMethod.GET) && httpRequest.getPath().equals("/create")) {
                User user = User.from(httpRequest.getParameters());
                WebServer.userRepository.save(user);
                response302Header(dos, "/index.html");
                return;
            }
            else if (httpRequest.getMethod().isPost() && httpRequest.getPath().equals("/create")) {
                User user = User.from(httpRequest.getParameters());
                WebServer.userRepository.save(user);
                response302Header(dos, "/index.html");
                return;
            }
            else if (httpRequest.getMethod().equals(HttpMethod.GET) && httpRequest.getPath().equals("/user/list")) {
                if (httpRequest.checkLogin()) {
                    Set<User> users = WebServer.userRepository.getUsers();
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html>");
                    sb.append("<body>");
                    for (User user : users) {
                        sb.append(user.toString());
                    }
                    sb.append("</body>");
                    sb.append("</html>");

                    body = sb.toString().getBytes();
                } else {
                    response302Header(dos, "/login.html");
                    return;
                }
            }



            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }




    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CSS(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + location);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithLogin(DataOutputStream dos, String location, boolean isLogin) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: logined=" + isLogin + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
