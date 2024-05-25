package de.teatime08.config;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public String selectedProviderDomain = "http://google.com";

    public Map<String, String> customProviders = new HashMap<>();

    public Config() {
    }
}
