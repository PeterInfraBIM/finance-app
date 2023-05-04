package nl.infrabim.financeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class FinanceAppApplication {

    public static List<File> csvFiles = null;

    public static void main(String[] args) {
        csvFiles = new ArrayList<>();
        try {
            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    File csvFile = new File(args[i]);
                    csvFiles.add(csvFile);
                }
            } else {
                Path start = new File(System.getProperty("user.dir")).toPath();
                Files.list(start)
                        .filter((p) -> p.toString().endsWith(".csv"))
                        .forEach((p) -> csvFiles.add(new File(p.toString())));
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        SpringApplication.run(FinanceAppApplication.class, args);
    }
}

