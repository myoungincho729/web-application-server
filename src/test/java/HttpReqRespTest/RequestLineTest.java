package HttpReqRespTest;

import org.junit.Assert;
import org.junit.Test;
import refactoring.HttpMethod;
import refactoring.RequestLine;

public class RequestLineTest {

    @Test
    public void create_method() {
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
        Assert.assertEquals(HttpMethod.GET, line.getMethod());
        Assert.assertEquals("/index.html", line.getPath());
        Assert.assertEquals("HTTP/1.1", line.getVersion());

        line = new RequestLine("POST /index.html HTTP/1.1");
        Assert.assertEquals(HttpMethod.POST, line.getMethod());
        Assert.assertEquals("/index.html", line.getPath());
        Assert.assertEquals("HTTP/1.1", line.getVersion());
    }

    @Test
    public void withParameter() {
        RequestLine line = new RequestLine("GET /create?name=myoungin&password=1234 HTTP/1.1");
        Assert.assertEquals(HttpMethod.GET, line.getMethod());
        Assert.assertEquals("/create", line.getPath());
        Assert.assertEquals("HTTP/1.1", line.getVersion());
        Assert.assertEquals("myoungin", line.getParams().get("name"));
        Assert.assertEquals("1234", line.getParams().get("password"));

    }
}
