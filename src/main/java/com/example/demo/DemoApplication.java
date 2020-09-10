package com.example.demo;

import main.java.com.example.demo.DecoratedMap;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	public static final String PROPERTIES_FILENAME = "config.properties";
	public static final String PROPERTIES_SEPARATOR = "=";
	private static Logger LOG = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws IOException {
        if(args.length == 0){
            System.out.println("Usage: java -jar <this-jar-file> <path to your leaf>");
            System.out.println("       properties will be traversed from leaf to last folder (excluding current)");
            return;
        }

        Map<String, String> properties = new DecoratedMap();
        Path systemPath = Paths.get("").toAbsolutePath();
        Path path = Paths.get(systemPath.toString(), FileSystems.getDefault().getSeparator(), args[0]);

        do {
            readAndUpdateMap(properties, path);
            path = path.getParent();
        } while (!path.equals(systemPath));

        printPropertiesSorted(properties);

    }

    private void printPropertiesSorted(Map<String, String> properties) {
        properties.keySet().stream().sorted().forEach(s -> {
            System.out.printf("%s=%s\n", s, properties.get(s));
        });
    }

    void readAndUpdateMap(Map<String, String> map, Path path) throws IOException {
        File file = new File(path.toAbsolutePath().toFile(), FileSystems.getDefault().getSeparator() + PROPERTIES_FILENAME);
        if (!file.canRead()) {
            return;
        }
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        StringSubstitutor stringSubstitutor = new StringSubstitutor(map);
        while ((line = br.readLine()) != null) {
            String[] propertyRow = line.split(PROPERTIES_SEPARATOR);
            if (propertyRow.length != 2) {
                LOG.warn("Line {} cannot be split to two parts", line);
                continue;
            }
            if (map.containsKey(propertyRow[0])) {
                LOG.info("Key {} is already in map, skipping", propertyRow[0].toLowerCase());
                continue;
            }
            map.put(propertyRow[0], stringSubstitutor.replace(propertyRow[1]));
        }
    }
}
