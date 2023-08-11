package webserver;

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

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	
	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
		
		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream();
			 BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			DataOutputStream dos = new DataOutputStream(out);

			// 헤더와 라인 구별
			HttpHeader httpHeader = parseHeader(br);
//			httpHeader.print();

			// body 구현
			BufferedReader brf = null;
			String content;
			byte[] body = new byte[0];

			if (httpHeader.getUriString().equals("/favicon.ico")) {
				return;
			}
			else if (httpHeader.getHeaderMap().get("Accept").startsWith("text/css") && httpHeader.getUriString().startsWith("/stylesheets")) {
				body = Files.readAllBytes(Path.of("./webapp" + httpHeader.getUriString()));
				response200CSS(dos, body.length);
				responseBody(dos, body);
				return;
			}
			else if (httpHeader.getUriString().equals("/index.html")) {
				body = Files.readAllBytes(Path.of("./webapp/index.html"));
			}
			else if (httpHeader.getUriString().equals("/form.html")) {
				body = Files.readAllBytes(Path.of("./webapp/form.html"));
			}
			else if (httpHeader.getUriString().equals("/login.html")) {
				body = Files.readAllBytes(Path.of("./webapp/login.html"));
			}
			else if (httpHeader.getHttpMethod().equals("GET") && httpHeader.getUriString().equals("/login")) {
				if (WebServer.userRepository.tryLogin(httpHeader.getQueryStringMap().get("userId"),
						httpHeader.getQueryStringMap().get("password"))) {
					response302HeaderWithLogin(dos, "/index.html", true);
					return;
				} else {
					response302HeaderWithLogin(dos, "/user/login_failed.html", false);
					return;
				}
			}
			else if (httpHeader.getHttpMethod().equals("GET") && httpHeader.getUriString().equals("/create")) {
				User user = User.from(httpHeader.getQueryStringMap());
				WebServer.userRepository.save(user);
				response302Header(dos, "/index.html");
				return;
			}
			else if (httpHeader.getHttpMethod().equals("POST") && httpHeader.getUriString().equals("/create")) {
				User user = User.from(httpHeader.getQueryStringMap());
				WebServer.userRepository.save(user);
				response302Header(dos, "/index.html");
				return;
			}
			else if (httpHeader.getHttpMethod().equals("GET") && httpHeader.getUriString().equals("/user/list")) {
				if (httpHeader.checkLogin()) {
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


	private HttpHeader parseHeader(BufferedReader br) throws IOException {
		List<String> headerStrings = new ArrayList<>();
		String line = "";
		while (true) {
			line = br.readLine();
			System.out.println(line);
			if (line == null) break;
			if (line.length() == 0) break;
			headerStrings.add(line);
		}
		HttpHeader httpHeader = HttpHeader.from(headerStrings);
		int bodyLength = httpHeader.getContainBodyLength();
		if (bodyLength > 0) {
			String body = IOUtils.readData(br, bodyLength);
			httpHeader.setBody(body);
		}
		if (httpHeader.getHeaderMap().containsKey("Cookie")) {
			httpHeader.setCookies();
		}
		return httpHeader;
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
