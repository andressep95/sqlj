package cl.playground;

import cl.playground.generator.ConfigReader;
import cl.playground.generator.InterfaceGenerator;
import cl.playground.parser.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

@Command(name = "sqlj", mixinStandardHelpOptions = true, version = "sqlj 1.0",
        description = "Genera interfaces basadas en un archivo de configuración YAML.")
public class Main implements Callable<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    @Parameters(index = "0", description = "El comando a ejecutar", arity = "1")
    private String command;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        if (!"init".equalsIgnoreCase(command)) {
            logger.error("Comando no reconocido. Uso: sqlj init");
            return 1;
        }

        String currentDir = System.getProperty("user.dir");
        File yamlFile = new File(currentDir, "sqlj.yaml");

        if (!yamlFile.exists()) {
            logger.error("El archivo sqlj.yaml no fue encontrado en el directorio actual.");
            return 1;
        }

        try {
            // Leer la configuración desde sqlj.yaml
            Config config = ConfigReader.readConfig(yamlFile.getPath());

            // Generar interfaces basadas en la configuración utilizando InterfaceGenerator
            InterfaceGenerator.generateInterfaces(config);

            logger.info("Interfaces generadas exitosamente.");
            return 0; // Indicar que el comando se ejecutó con éxito
        } catch (Exception e) {
            logger.error("Ocurrió un error al generar las interfaces:", e);
            return 1; // Indicar que hubo un error
        }
    }

}
