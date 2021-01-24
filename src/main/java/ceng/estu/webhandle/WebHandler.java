package ceng.estu.webhandle;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

/**
 * @author reuzun
 */
public class WebHandler {

    private static WebClient webClient;

    static {
        webClient = WebUtililities.getSilencedWebClient();
        CookieManager cookieManager = webClient.getCookieManager();
        cookieManager.setCookiesEnabled(true);
        //Since we dont want any logging
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getGlobal().setLevel(java.util.logging.Level.OFF);
        java.util.logging.Logger.getLogger("org.apache").setLevel(java.util.logging.Level.OFF);
    }

    public static String getSyncTubePage() {
        HtmlPage page = null;
        try {
             page = webClient.getPage("https://sync-tube.de/create");
        }catch (Exception e){
            System.out.println("Page not found.");
        }
        webClient.waitForBackgroundJavaScript(2000);
        return String.valueOf(page.getUrl());
    }

}
