package nl.infrabim.financeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class FinanceAppApplication {

    public static List<File> csvFiles = null;

    public static void main(String[] args) {
        if (args.length > 0) {
            csvFiles = new ArrayList<>();
            try {
                for (int i = 0; i < args.length; i++) {
                    File csvFile = new File(args[i]);
                    csvFiles.add(csvFile);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        SpringApplication.run(FinanceAppApplication.class, args);
    }

}
