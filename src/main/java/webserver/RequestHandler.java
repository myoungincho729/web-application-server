package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

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
			System.out.println(httpHeader.toString());
			if (httpHeader.getUri().equals("/favicon.ico")) {
				return;
			}
			BufferedReader brf = new BufferedReader(new FileReader("./webapp" + httpHeader.getUri()));

			String content;
			String body = "";
			while ((content = brf.readLine()) != null) {
				body += content + '\n';
			}
			if (body.equals("")) {
				body = "Hello world";
			}

			byte[] body2 = body.getBytes();
			response200Header(dos, body2.length);
			responseBody(dos, body2);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private HttpHeader parseHeader(BufferedReader br) throws IOException {
		List<String> headerStrings = new ArrayList<>();
		String line = "";
		while (true) {
			line = br.readLine();
			if (line.length() == 0) break;
			headerStrings.add(line);
		}
		return HttpHeader.from(headerStrings);
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
