package cl.playground;

import cl.playground.generator.ConfigReader;
import cl.playground.generator.InterfaceGenerator;
import cl.playground.parser.Config;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: sqlj <command> <config-file-path>");
            System.exit(1);
        }

        String command = args[0];
        String configFilePath = args[1];

        switch (command) {
            case "init":
                init(configFilePath);
                break;
            default:
                System.err.println("Unknown command: " + command);
                System.exit(1);
        }
    }

    private static void init(String configFilePath) {
        Config config = ConfigReader.readConfig(configFilePath);
        InterfaceGenerator.generateInterfaces(config);
    }
}
