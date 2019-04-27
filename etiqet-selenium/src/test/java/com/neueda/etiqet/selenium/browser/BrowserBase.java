package com.neueda.etiqet.selenium.browser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;

@XmlRootElement(name = "Browsers")
public class BrowserBase {

    private static Logger logger = Logger.getLogger(BrowserBase.class);
    private static HashMap<String, Browser> browserMap;
    private static List<Browser> allBrowsers;
    private static String currentBrowser = "default";
    @XmlElements({
        @XmlElement(name = "Firefox", type = Firefox.class),
        @XmlElement(name = "Chrome", type = Chrome.class)
    })
    private List<Browser> browsers;

    private BrowserBase() {
    }

    public static void selectBrowser(String browserName) {
        currentBrowser = browserName;
    }

    public static void selectBrowser(int browserIndex) {
        currentBrowser = allBrowsers.get(browserIndex).getName();
    }

    public static Browser getCurrentBrowser() {
        return browserMap.get(currentBrowser);
    }

    public static List<Browser> getBrowsers() {
        return allBrowsers;
    }

    public static Browser getBrowserByName(String name) {
        return browserMap.get(name);
    }

    private void afterUnmarshal(Unmarshaller u, Object parent) {
        browserMap = new HashMap<>();
        allBrowsers = new ArrayList<>();
        for (Browser browser : browsers) {
            browserMap.put(browser.getName(), browser);
            allBrowsers.add(browser);
            logger.info("Browser registered: " + browser.getName());
        }
    }
}
