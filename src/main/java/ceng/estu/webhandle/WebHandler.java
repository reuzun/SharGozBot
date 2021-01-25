package ceng.estu.webhandle;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
        for(int i = 1 ; i < 14 ; i++) {
            lastNumber++;
            Elements elements = doc.select("#partial-index > ul > li:nth-child("+ i + ")");
            for(Element e : elements) {
                Element linkTag = e.getElementsByTag("a").first();
                String url = "";
                try {
                     url = linkTag.absUrl("href");
                }catch (Exception ex){
                    lastNumber--;
                    continue;
                }

                sb.append(lastNumber+ "-" + url+"\n\n");

            }
        }
        return sb.toString();
    }

}
