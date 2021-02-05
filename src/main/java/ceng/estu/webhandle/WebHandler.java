package ceng.estu.webhandle;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        } catch (Exception e) {
            System.out.println("Page not found.");
        }
        return String.valueOf(page.getUrl());
    }

    public static String getRandomQuote() throws IOException {
        webClient.getCookieManager().setCookiesEnabled(true);

        String quote = null;

        HtmlPage page = null;
        try {
            page = webClient.getPage("https://miniwebtool.com/random-quote-generator/");
        } catch (Exception e) {
            System.out.println("Page not found.");
        }



        HtmlSubmitInput btn = page.getFirstByXPath("/html/body/div[7]/div[1]/div[7]/div/form/table/tbody/tr[2]/td/input");

        page = btn.click();

        DomText web_Quote = page.getFirstByXPath("/html/body/div[7]/div[1]/div[8]/div[4]/div[1]/div/div[1]/text()");
        HtmlAnchor web_Author = page.getFirstByXPath("/html/body/div[7]/div[1]/div[8]/div[4]/div[1]/div/div[2]/a");

        quote = web_Quote + " ---" +web_Author.getTextContent();

        return quote;
    }

}
