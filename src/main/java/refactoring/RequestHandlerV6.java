package refactoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refactoring.controller.Controller;
import refactoring.controller.RequestMapping;
import session.HttpSessions;
import util.HttpRequestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

//HttpRequest + HttpResponse + 요청URL RequestMapping + Controller 리팩토링 + HttpSession
public class RequestHandlerV6 extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandlerV6.class);

    private Socket connection;

    public RequestHandlerV6(Socket connectionSocket) {
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

            if (httpRequest.getCookies().getCookie("JSESSIONID") == null) {
                httpResponse.addHeader(
                        "Set-Cookie",
                        "JSESSIONID=" + HttpSessions.getSession(UUID.randomUUID().toString()));
            }

            String path = httpRequest.getPath();

            Controller controller = RequestMapping.getController(path);
            if (controller == null) {
                path = getDefaultPath(path);
                httpResponse.forward(path);
            } else {
                controller.service(httpRequest, httpResponse);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getSessionId(String cookie) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookie);
        return cookies.get("JSESSIONID");
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}

