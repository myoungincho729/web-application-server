package session;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
    private Map<String, String> values = new HashMap<>();
    private String id;

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, String value) {
        values.put(name, value);
    }

    public String getAttribute(String name) {
        return values.get(name);
    }

    public void removeAttribute(String name) {
        values.remove(name);
    }

    public void invalidate() {
        HttpSessions.remove(id);
    }
}
