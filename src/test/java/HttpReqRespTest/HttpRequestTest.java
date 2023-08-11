package HttpReqRespTest;

import org.junit.Assert;
import org.junit.Test;
import refactoring.HttpMethod;
import refactoring.HttpRequest;

import java.io.FileInputStream;
import java.io.InputStream;

public class HttpRequestTest {
    private String testDir = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception {
        InputStream in = new FileInputStream(testDir + "Http_Get.txt");
        HttpRequest req = new HttpRequest(in);

        Assert.assertEquals(HttpMethod.GET, req.getMethod());
        Assert.assertEquals("/user/create", req.getPath());
        Assert.assertEquals("keep-alive", req.getHeader("Connection"));
        Assert.assertEquals("myoungin", req.getParameter("userId"));
    }

    @Test
    public void request_POST() throws Exception {
        InputStream in = new FileInputStream(testDir + "Http_Post.txt");
        HttpRequest req = new HttpRequest(in);

        Assert.assertEquals(HttpMethod.POST, req.getMethod());
        Assert.assertEquals("/user/create", req.getPath());
        Assert.assertEquals("keep-alive", req.getHeader("Connection"));
        Assert.assertEquals("myoungin", req.getParameter("userId"));
    }

    @Test
    public void getLength() {
        System.out.println("userId=myoungin&password=password&name=mi".length());
    }
}