package webserver;

import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeader {
    private String httpMethod;
    private String uriString;
    private String httpVersion;
    private Map<String, String> headerMap;
    private Map<String, String> queryStringMap;
    private Map<String, String> cookieMap;
    private String body;


    public HttpHeader(String httpMethod, String uriString, String httpVersion, Map<String, String> headerMap, Map<String, String> queryStringMap) {
        this.httpMethod = httpMethod;
        this.uriString = uriString;
        this.httpVersion = httpVersion;
        this.headerMap = headerMap;
        this.queryStringMap = queryStringMap;
    }

    public static HttpHeader from(List<String> headerStrings) {
        String[] firstLine = headerStrings.get(0).split(" ");
        String httpMethod = firstLine[0];

        String[] uriQuery = firstLine[1].split("[?]");
        String uriString = uriQuery[0];
        String queryString = null;
        if (uriQuery.length == 2) {
            queryString = uriQuery[1];
        }
        Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(queryString);

        String httpVersion = firstLine[2];
        Map<String, String> headerMap = parseHttpHeaders(headerStrings);
        return new HttpHeader(httpMethod, uriString, httpVersion, headerMap, queryStringMap);
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

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUriString() {
        return uriString;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public Map<String, String> getQueryStringMap() {
        return queryStringMap;
    }

    public void setBody(String body) {
        this.body = body;
        if (headerMap.get("Content-Type").equals("application/x-www-form-urlencoded")) {
            queryStringMap = HttpRequestUtils.parseQueryString(body);
        }
    }

    public void setCookies() {
        if (this.headerMap.get("Cookie") == null) {
            return;
        }
        this.cookieMap = HttpRequestUtils.parseCookies(this.headerMap.get("Cookie"));
    }
    public void print() {
        System.out.println("httpMethod = " + httpMethod);
        System.out.println("uriString = " + uriString);
        System.out.println("httpVersion = " + httpVersion);
        for (String key : queryStringMap.keySet()) {
            System.out.println(key + " = " + queryStringMap.get(key));
        }
        for (String key : headerMap.keySet()) {
            System.out.println(key + " = " + headerMap.get(key));
        }
        System.out.println("body = " + body);
    }
    public String toString() {
        return String.format("Method : [%s]\nURI : [%s]\nVersion : [%s]\n", httpMethod, uriString, httpVersion);
    }

    public int getContainBodyLength() {
        if (headerMap.containsKey("Content-Length") == false) {
            return 0;
        }
        return Integer.valueOf(headerMap.get("Content-Length"));
    }

    public boolean checkLogin() {
        return cookieMap.containsKey("logined") && cookieMap.get("logined").equals("true");
    }
}
