package cl.playground.generator;

import cl.playground.parser.Config;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class InterfaceGenerator {

    public static void generateInterfaces(Config config) {
        // Obtener la configuración desde el primer elemento de la lista de SQL
        String entityPackage = config.getSql().get(0).getGen().getJava().getEntityPackage();
        String outputDir = config.getSql().get(0).getGen().getJava().getOut();
        String typeRepository = config.getSql().get(0).getGen().getJava().getTypeRepository();  // Lee el tipo de repositorio

        try {
            Set<Class<?>> entities = getClasses(entityPackage);

            for (Class<?> entity : entities) {
                if (entity.isAnnotationPresent(Entity.class)) {
                    generateInterface(entity, outputDir, entityPackage, typeRepository);
                }
            }

            System.out.println("Interfaces generated successfully.");

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void generateInterface(Class<?> entity, String outputDir, String entityPackage, String typeRepository) throws IOException {
        String className = entity.getSimpleName() + "Repository";
        String packageName = outputDir.replace("src/main/java/", "").replace('/', '.');
        Class<?> idClass = getIdClass(entity);

        // Define la implementación del repositorio basado en el tipo configurado
        String repositoryExtension;
        String repositoryImport;
        if ("jpa".equalsIgnoreCase(typeRepository)) {
            repositoryExtension = "JpaRepository<" + entity.getSimpleName() + ", " + idClass.getSimpleName() + ">";
            repositoryImport = "import org.springframework.data.jpa.repository.JpaRepository;";
        } else if ("generic".equalsIgnoreCase(typeRepository)) {
            repositoryExtension = "GenericRepository<" + entity.getSimpleName() + ", " + idClass.getSimpleName() + ">";
            repositoryImport = findGenericRepository();
        } else {
            throw new IllegalArgumentException("Invalid typeRepository value: " + typeRepository + ". Expected 'jpa' or 'generic'.");
        }

        // Log the details of the entity being processed
        System.out.println("Generating interface for entity: " + entity.getName() + " with ID type: " + idClass.getSimpleName());

        String content = "package " + packageName + ";\n\n" +
                "import " + entityPackage + "." + entity.getSimpleName() + ";\n" +
                repositoryImport + "\n" +
                "import org.springframework.stereotype.Repository;\n" +
                "import " + idClass.getName() + ";\n\n" +
                "@Repository\n" +
                "public interface " + className + " extends " + repositoryExtension + " {\n" +
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

    private static String findGenericRepository() {
        try {
            // Try to locate the class in the classpath
            Class<?> genericRepositoryClass = Class.forName("cl.playground.querygenerator.util.repositorys.GenericRepository");
            return "import " + genericRepositoryClass.getName() + ";";
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("GenericRepository class not found in the classpath. Please ensure it is available.");
        }
    }

    private static Class<?> getIdClass(Class<?> entity) {
        for (Field field : entity.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                // Check if it's a primitive type and convert to the corresponding wrapper class
                Class<?> fieldType = field.getType();
                if (fieldType.isPrimitive()) {
                    if (fieldType == long.class) return Long.class;
                    if (fieldType == int.class) return Integer.class;
                    // Add other primitive types as needed
                }
                return fieldType;
            }
        }
        throw new IllegalStateException("No field with @Id annotation found in " + entity.getName());
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