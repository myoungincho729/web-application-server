package refactoring;

import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private String body;

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String[] firstLine = br.readLine().split(" ");
        method = firstLine[0];

        parameters = new HashMap<>();
        int index = firstLine[1].indexOf("?");
        if (index == -1) {
            path = firstLine[1];
        } else {
            path = firstLine[1].substring(0, index);
            parameters = HttpRequestUtils.parseQueryString(firstLine[1].substring(index + 1));
        }
        version = firstLine[2];

        headers = new HashMap<>();
        while (true) {
            String header = br.readLine();
            if (header.isEmpty()) {
                break;
            }
            String[] headerKeyVal = header.split(": ");
            headers.put(headerKeyVal[0], headerKeyVal[1]);
        }
        if (headers.containsKey("Content-Length")) {
            body = IOUtils.readData(br, Integer.valueOf(headers.get("Content-Length")));
            if (getHeader("Content-Type").equals("application/x-www-form-urlencoded")) {
                parameters = HttpRequestUtils.parseQueryString(body);
            }
        }
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
