package com.neueda.etiqet.selenium.fixture.stepdefs;

import com.neueda.etiqet.selenium.fixture.SeleniumHandlers;
import cucumber.api.java.en.Given;

public class Initialization {

    @Given("^I? ?open (?:the)? ?browser$")
    public void openBrowser() {
        SeleniumHandlers.openBrowser();
    }

    @Given("(?:the)? ?\"([^\"]*)\" browser is open")
    public void openGivenWebBrowser(String browserName) throws Throwable {
        SeleniumHandlers.openBrowser(browserName);
    }

    /**
     * Opens a url
     *
     * @param url should be a fully qualified url such as https://www.google.com rather than google.com
     */
    @Given("^I? ?(?:(?:go to)|(?:visit)) (?:the)? ?(?:website|url) \"([^\"]*)\"$")
    public void goToUrl(String url) {
        SeleniumHandlers.goToUrl(url);
    }

    @Given("^(?:the)? ?window is maximized$")
    public void maximizeWindow() {
        SeleniumHandlers.maximizeWindow();
    }

    // ALIASES

    @Given("^I (?:choose|select|am using) ?(?:the)? browser \"([^\"]*)\"$")
    public void selectDriver(String browserName) throws Throwable {
        openGivenWebBrowser(browserName);
    }

    @Given("^I? ?maximize (?:the)? ?window$")
    public void maximizeWindowAlias() {
        maximizeWindow();
    }

    @Given("^I? ?launch (?:the )? ?browser$")
    public void openBrowserAlias() {
        openBrowser();
    }

    @Given("^(?:the)? ?browser (?:is|has been) (?:launched|(?:opened|open))$")
    public void openBrowserAlias2() {
        openBrowser();
    }
}
