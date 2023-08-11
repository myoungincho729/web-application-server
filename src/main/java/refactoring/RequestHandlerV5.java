package refactoring;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import refactoring.controller.Controller;
import refactoring.controller.RequestMapping;
import webserver.WebServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

//HttpRequest + HttpResponse + 요청URL RequestMapping + Controller 리팩토링
public class RequestHandlerV5 extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandlerV5.class);

    private Socket connection;

    public RequestHandlerV5(Socket connectionSocket) {
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

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}

