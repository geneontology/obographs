package org.geneontology.obographs.cli.commands;

import org.geneontology.obographs.core.io.OgJsonReader;
import org.geneontology.obographs.core.io.OgYamlReader;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "validate", description = "validates an obograph file")
public class Validate implements Callable<Integer> {

    @Parameters(arity = "1..*", paramLabel = "FILE", description = "JSON/YAML file(s) to validate.")
    private List<Path> inputPaths;

    @Override
    public Integer call() {
        int exitCode = 0;
        for (Path inputFile : inputPaths) {
            try {
                var fileType = detectFileType(inputFile);
                System.err.print(inputFile);
                if ("json".equals(fileType)) {
                    OgJsonReader.readFile(inputFile.toFile());
                    System.err.print(" - OK\n");
                } else if ("yaml".equals(fileType)) {
                    OgYamlReader.readFile(inputFile.toFile());
                    System.err.print(" - OK\n");
                } else if ("dir".equals(fileType)) {
                    System.err.println(" - is directory");
                } else {
                    System.err.println(" - invalid file type");
                }
            } catch (Exception e) {
                System.err.print(" - ERROR: ");
                System.err.print(e.getMessage() + "\n");
                exitCode = 1;
            }
        }
        return exitCode;
    }

    private String detectFileType(Path file) {
        if (Files.isDirectory(file)) {
            return "dir";
        }
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            char[] firstFew = new char[2];
            if (reader.read(firstFew) != -1) {
                if (firstFew[0] == '{') {
                    return "json";
                } else if (firstFew[0] == '-' && firstFew[1] == '-') {
                    return "yaml";
                }
            }
        } catch (IOException e) {
            // swallow
        }
        return "";
    }


}
