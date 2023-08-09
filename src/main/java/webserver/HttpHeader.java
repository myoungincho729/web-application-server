package webserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {
    private String httpMethod;
    private String uriString;
    private String httpVersion;
    private Map<String, String> headerMap;

    public HttpHeader(String httpMethod, String uriString, String httpVersion, Map<String, String> headerMap) {
        this.httpMethod = httpMethod;
        this.uriString = uriString;
        this.httpVersion = httpVersion;
        this.headerMap = headerMap;
    }

    public static HttpHeader from(List<String> headerStrings) {
        String[] firstLine = headerStrings.get(0).split(" ");
        String httpMethod = firstLine[0];
        String uriString = firstLine[1];
        String httpVersion = firstLine[2];
        Map<String, String> headerMap = parseHttpHeaders(headerStrings);
        return new HttpHeader(httpMethod, uriString, httpVersion, headerMap);
    }

    private static Map<String, String> parseHttpHeaders(List<String> headerStrings) {
        Map<String, String> headerMap = new HashMap<>();
        int tmp = 0;
        for (String header : headerStrings) {
            if (tmp == 0) {
                tmp++;
                continue;
            }
            String[] headerCombi = header.split(": ");
            headerMap.put(headerCombi[0], headerCombi[1]);
        }
        return headerMap;
    }

    public String getUri() {
        return uriString;
    }

    public String toString() {
        return String.format("Method : [%s]\n" +
                "URI : [%s]\n" +
                "Version : [%s]\n", httpMethod, uriString, httpVersion);
    }
}
