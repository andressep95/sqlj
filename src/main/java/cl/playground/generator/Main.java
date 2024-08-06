package cl.playground.generator;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && "init".equals(args[0])) {
            try {
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", "sh $(dirname $(realpath $0))/install.sh");
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                process.waitFor();
                System.out.println("Instalación completada.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Uso: sqlj init");
        }
    }
}
