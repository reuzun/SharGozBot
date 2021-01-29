package ceng.estu.webhandle;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLTextAreaElement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import reactor.util.annotation.NonNull;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.util.StringTokenizer;

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
        //webClient.waitForBackgroundJavaScript(500);
        return String.valueOf(page.getUrl());
    }

    public static String eksiSozlukGundem() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://eksisozluk.com/").get();
        } catch (Exception e) {
        }
        StringBuilder sb = new StringBuilder();

        int lastNumber = 0;
        for (int i = 1; i < 14; i++) {
            lastNumber++;
            Elements elements = doc.select("#partial-index > ul > li:nth-child(" + i + ")");
            for (Element e : elements) {
                Element linkTag = e.getElementsByTag("a").first();
                String url = "";
                try {
                    url = linkTag.absUrl("href");
                } catch (Exception ex) {
                    lastNumber--;
                    continue;
                }

                sb.append(lastNumber + "-" + url + "\n\n");

            }
        }
        return sb.toString();
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

    /*public static void main(String[] args) throws IOException {
        System.out.println(getRandomQuote());
    }*/
}
