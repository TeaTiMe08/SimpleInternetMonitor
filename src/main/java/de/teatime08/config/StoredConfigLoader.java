package de.teatime08.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;

public class StoredConfigLoader {
    private static final String configFileName = System.getProperty("user.home") + File.separator + ".simpleInternetMonitorConfig.json";

    private Config config;
    private HashSet<IUpdateConfiguration> iUpdateConfigurations = new HashSet<>();


    public StoredConfigLoader() throws IOException {
        config = load();
    }

    public void reLoad() throws IOException {
        load();
        iUpdateConfigurations.forEach(iUpdateConfiguration -> iUpdateConfiguration.configUpdated(config));
    }

    public void store() throws IOException {
        store(config);
        iUpdateConfigurations.forEach(iUpdateConfiguration -> iUpdateConfiguration.configUpdated(config));
    }

    public Config getConfig() {
        return config;
    }

    public void subscribe(IUpdateConfiguration iUpdateConfiguration) {
        this.iUpdateConfigurations.add(iUpdateConfiguration);
    }

    public static Config load() throws IOException {
        File configFile = new File(configFileName);
        if ( ! configFile.exists()) {
            store(new Config()); // store initial
            System.out.println("No initial configuration found. Stored default config.");
        }
        String jsonConfigString = new String(Files.readAllBytes(configFile.toPath()));
        Config config = new Gson().fromJson(jsonConfigString, Config.class);
        System.out.println("Loaded config: " + System.lineSeparator() + new GsonBuilder().setPrettyPrinting().create().toJson(config));
        return config;
    }

    public static void store(Config newConfig) throws IOException {
        File configFile = new File(configFileName);
        String jsonConfigString = new Gson().toJson(newConfig);
        try (FileWriter fileWriter = new FileWriter(configFile)) {
            fileWriter.write(jsonConfigString);
        }
    }

    public interface IUpdateConfiguration {
        public void configUpdated(Config config);
    }

}
