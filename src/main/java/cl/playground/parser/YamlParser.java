package cl.playground.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YamlParser {

    private static final String INDENT = "  ";

    public Config parse(String filePath) throws IOException {
        Config config = new Config();
        List<Sql> sqlList = new ArrayList<>();
        config.setSql(sqlList);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (isSkippable(line)) {
                    continue;
                }
                if (line.startsWith("version:")) {
                    config.setVersion(extractValue(line));
                } else if (line.startsWith("- engine:")) {
                    Sql sql = new Sql();
                    sql.setEngine(extractValue(line));
                    sqlList.add(sql);
                    parseGen(br, sql);
                }
            }
        }

        return config;
    }

    private void parseGen(BufferedReader br, Sql sql) throws IOException {
        String line;
        while ((line = br.readLine()) != null && line.startsWith(INDENT)) {
            line = line.trim();
            if (isSkippable(line)) {
                continue;
            }
            if (line.startsWith("gen:")) {
                Gen gen = new Gen();
                sql.setGen(gen);
                parseJavaGen(br, gen);
            }
        }
    }

    private void parseJavaGen(BufferedReader br, Gen gen) throws IOException {
        String line;
        while ((line = br.readLine()) != null && line.startsWith(INDENT + INDENT)) {
            line = line.trim();
            if (isSkippable(line)) {
                continue;
            }
            if (line.startsWith("java:")) {
                JavaGen javaGen = new JavaGen();
                gen.setJava(javaGen);
                parseJavaGenFields(br, javaGen);
            }
        }
    }

    private void parseJavaGenFields(BufferedReader br, JavaGen javaGen) throws IOException {
        String line;
        while ((line = br.readLine()) != null && line.startsWith(INDENT + INDENT + INDENT)) {
            line = line.trim();
            if (isSkippable(line)) {
                continue;
            }
            if (line.startsWith("pkg:")) {
                javaGen.setPackage(extractValue(line));
            } else if (line.startsWith("entityPackage:")) {
                javaGen.setEntityPackage(extractValue(line));
            } else if (line.startsWith("out:")) {
                javaGen.setOut(extractValue(line));
            } else if (line.startsWith("typeRepository:")) {  // Añadido: lectura de typeRepository
                javaGen.setTypeRepository(extractValue(line));
            }
        }
    }

    private String extractValue(String line) {
        return line.split(":")[1].trim().replace("\"", "");
    }

    private boolean isSkippable(String line) {
        return line.isEmpty() || line.startsWith("#");
    }
}