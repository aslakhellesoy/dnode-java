package dnode.webbit;

import com.gargoylesoftware.htmlunit.AlertHandler;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import dnode.Callback;
import dnode.DNode;
import org.junit.Ignore;
import org.junit.Test;
import webbit.WebServer;
import webbit.handler.EmbeddedResourceHandler;
import webbit.netty.NettyWebServer;

import java.io.IOException;

import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class WebbitIntegrationTest {
    public static class Cat {
        public void cat(Callback cb) {
            System.out.println("cb = " + cb);
            cb.call("GROWL");
        }
    }

    @Test
    @Ignore
    public void should_work_with_browser() throws IOException, InterruptedException {
        WebServer server = new NettyWebServer(6061);
        new DNode(new Cat()).listen(new WebbitServer(server));
        server.add("/.*", new EmbeddedResourceHandler("dnode/js", newFixedThreadPool(4)));
        server.start();

        WebClient client = new WebClient(BrowserVersion.INTERNET_EXPLORER_8);
        client.setAlertHandler(new AlertHandler() {
            @Override
            public void handleAlert(Page page, String message) {
                System.out.println("alert = " + message);
            }
        });
        HtmlPage page = client.getPage("http://localhost:6061/");

        sleep(5000);
        // Expected to see GROWL in stdout at least, but not happening...
    }
}
