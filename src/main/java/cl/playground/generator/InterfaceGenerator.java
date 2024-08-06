package cl.playground.generator;

import cl.playground.parser.Config;
import jakarta.persistence.Entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class InterfaceGenerator {
    /*
    public static void main(String[] args) {
        Config config = ConfigReader.readConfig("sqlj.yaml");
        generateInterfaces(config);
    }
    */
    public static void generateInterfaces(Config config) {
        String entityPackage = config.getSql().get(0).getGen().getJava().getEntityPackage();
        String outputDir = config.getSql().get(0).getGen().getJava().getOut();

        try {
            Set<Class<?>> entities = getClasses(entityPackage);

            for (Class<?> entity : entities) {
                if (entity.isAnnotationPresent(Entity.class)) {
                    generateInterface(entity, outputDir, entityPackage);
                }
            }

            System.out.println("Interfaces generated successfully.");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void generateInterface(Class<?> entity, String outputDir, String entityPackage) throws IOException {
        String className = entity.getSimpleName() + "Repository";
        String packageName = outputDir.replace("src/main/java/", "").replace('/', '.');
        Class<?> idClass = getIdClass(entity);

        // Log the details of the entity being processed
        System.out.println("Generating interface for entity: " + entity.getName() + " with ID type: " + idClass.getSimpleName());

        String content = "package " + packageName + ";\n\n" +
                "import " + entityPackage + "." + entity.getSimpleName() + ";\n" +
                "import cl.playground.querygenerator.util.repositorys.GenericRepository;\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "import java.lang." + idClass.getSimpleName() + ";\n\n" +
                "@Repository\n" +
                "public interface " + className + " extends GenericRepository<" + entity.getSimpleName() + ", " + idClass.getSimpleName() + "> {\n" +
                "}";

        // Create the directories if they don't exist
        File outputDirFile = new File(outputDir);
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs();
        }

        // Write the content to the file
        FileWriter writer = new FileWriter(outputDir + "/" + className + ".java");
        writer.write(content);
        writer.close();
    }

    private static Class<?> getIdClass(Class<?> entity) {
        return Integer.class; // Replace this with logic to determine the ID class dynamically
    }

    private static Set<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        String path = packageName.replace('.', '/');
        Set<Class<?>> classes;
        try (var stream = Files.walk(Paths.get("src/main/java/" + path))) {
            classes = stream
                    .filter(file -> file.toString().endsWith(".java"))
                    .map(file -> {
                        String className = file.toString()
                                .replace("src/main/java/", "")
                                .replace(".java", "")
                                .replace('/', '.');
                        try {
                            return Class.forName(className);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toSet());
        }
        return classes;
    }
}
