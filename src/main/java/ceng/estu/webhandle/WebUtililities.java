package ceng.estu.webhandle;

import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.css.parser.CSSParseException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.parser.HTMLParserListener;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

import java.net.MalformedURLException;

/**
 * @author reuzun
 */
class WebUtililities {
     protected static WebClient getSilencedWebClient(){
        WebClient webClient = new WebClient();
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.setCssErrorHandler(new CSSErrorHandler() {
            @Override
            public void warning(CSSParseException exception) throws CSSException {
            }
            @Override
            public void error(CSSParseException exception) throws CSSException {
            }
            @Override
            public void fatalError(CSSParseException exception) throws CSSException {
            }
        });
        webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
            @Override
            public void scriptException(HtmlPage page, ScriptException scriptException) {
            }
            @Override
            public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
            }
            @Override
            public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {
            }
            @Override
            public void loadScriptError(HtmlPage page, java.net.URL scriptUrl, Exception exception) {
            }
            @Override
            public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        webClient.setHTMLParserListener(new HTMLParserListener() {
            @Override
            public void error(String message, java.net.URL url, String html, int line, int column, String key) {
            }
            @Override
            public void warning(String message, java.net.URL url, String html, int line, int column, String key) {
            }
        });

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);

        return webClient;
    }
}
