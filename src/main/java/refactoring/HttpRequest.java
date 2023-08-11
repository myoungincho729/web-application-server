package refactoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private RequestLine requestLine;

    public HttpRequest(InputStream in) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            if (line == null) {
                return;
            }
            requestLine = new RequestLine(line);

            line = br.readLine();
            while (!line.equals("")) {
                log.debug("header : {}", line);
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }

            if ("POST".equals(getMethod())) {
                String body = IOUtils.readData(br, Integer.valueOf(headers.get("Content-Length")));
                if (getHeader("Content-Type").equals("application/x-www-form-urlencoded")) {
                    parameters = HttpRequestUtils.parseQueryString(body);
                }
            } else {
                parameters = requestLine.getParams();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String param) {
        return parameters.get(param);
    }

}
