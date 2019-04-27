package com.neueda.etiqet.selenium.browser;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FirefoxOptions")
public class FirefoxSettings extends Options {

    @XmlElementWrapper(name = "startup_arguments")
    @XmlElement(name = "argument")
    private List<String> startupArgs;

    public FirefoxSettings() {
        startupArgs = new ArrayList<>();
    }

    @Override
    public List<String> getStartupArgs() {
        return startupArgs;
    }
}
