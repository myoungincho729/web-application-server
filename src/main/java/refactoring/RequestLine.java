package refactoring;

import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {
    private HttpMethod method;
    private String path;
    private Map<String, String> queryStrings = new HashMap<>();
    private String version;
    public RequestLine(String line) {
        String[] reqLineParts = line.split(" ");

        if (reqLineParts.length != 3) {
            throw new IllegalArgumentException("request line 형식에 맞지 않습니다.");
        }
        method = HttpMethod.valueOf(reqLineParts[0]);

        int index = reqLineParts[1].indexOf("?");
        if (index == -1) {
            path = reqLineParts[1];
        } else {
            path = reqLineParts[1].substring(0, index);
            queryStrings = HttpRequestUtils.parseQueryString(reqLineParts[1].substring(index + 1));
        }
        version = reqLineParts[2];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return queryStrings;
    }

    public String getVersion() {
        return version;
    }
}
