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
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private String body;

    public HttpRequest(InputStream in) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            if (line == null) {
                return;
            }
            processRequestLine(line);

            line = br.readLine();
            while (!line.equals("")) {
                log.debug("header : {}", line);
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }

            if ("POST".equals(method)) {
                body = IOUtils.readData(br, Integer.valueOf(headers.get("Content-Length")));
                if (getHeader("Content-Type").equals("application/x-www-form-urlencoded")) {
                    parameters = HttpRequestUtils.parseQueryString(body);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void processRequestLine(String requestLine) {
        String[] reqLineParts = requestLine.split(" ");

        method = reqLineParts[0];

        int index = reqLineParts[1].indexOf("?");
        if (index == -1) {
            path = reqLineParts[1];
        } else {
            path = reqLineParts[1].substring(0, index);
            parameters = HttpRequestUtils.parseQueryString(reqLineParts[1].substring(index + 1));
        }
        version = reqLineParts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getParameter(String param) {
        return parameters.get(param);
    }

    public String getBody() {
        return body;
    }
}
