package cl.playground.generator;

import cl.playground.parser.Config;
import cl.playground.parser.YamlParser;


import java.io.IOException;

public class ConfigReader {

    public static Config readConfig(String configFilePath) {
        YamlParser parser = new YamlParser();
        try {
            return parser.parse(configFilePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read configuration file", e);
        }
    }
    /*
    public static void main(String[] args) {
        Config config = readConfig("sqlj.yaml");
        System.out.println(config);
    }
    */
}
