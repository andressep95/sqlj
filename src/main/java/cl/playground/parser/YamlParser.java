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
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                if (line.startsWith("version:")) {
                    config.setVersion(line.split(":")[1].trim().replace("\"", ""));
                } else if (line.startsWith("- engine:")) {
                    Sql sql = new Sql();
                    sql.setEngine(line.split(":")[1].trim().replace("\"", ""));
                    sqlList.add(sql);

                    while ((line = br.readLine()) != null && line.startsWith(INDENT)) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue; // Skip empty lines and comments
                        }
                        if (line.startsWith("gen:")) {
                            Gen gen = new Gen();
                            sql.setGen(gen);

                            while ((line = br.readLine()) != null && line.startsWith(INDENT + INDENT)) {
                                line = line.trim();
                                if (line.isEmpty() || line.startsWith("#")) {
                                    continue; // Skip empty lines and comments
                                }
                                if (line.startsWith("java:")) {
                                    JavaGen java = new JavaGen();
                                    gen.setJava(java);

                                    while ((line = br.readLine()) != null && line.startsWith(INDENT + INDENT + INDENT)) {
                                        line = line.trim();
                                        if (line.isEmpty() || line.startsWith("#")) {
                                            continue; // Skip empty lines and comments
                                        }
                                        if (line.startsWith("pkg:")) {
                                            java.setPackage(line.split(":")[1].trim().replace("\"", ""));
                                        } else if (line.startsWith("entityPackage:")) {
                                            java.setEntityPackage(line.split(":")[1].trim().replace("\"", ""));
                                        } else if (line.startsWith("out:")) {
                                            java.setOut(line.split(":")[1].trim().replace("\"", ""));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return config;
    }
}
