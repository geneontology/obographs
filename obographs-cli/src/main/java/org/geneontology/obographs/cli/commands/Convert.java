package org.geneontology.obographs.cli.commands;

import org.geneontology.obographs.core.io.OgJsonGenerator;
import org.geneontology.obographs.core.io.OgYamlGenerator;
import org.geneontology.obographs.core.model.GraphDocument;
import org.geneontology.obographs.owlapi.FromOwl;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Option;
import static picocli.CommandLine.Parameters;

@Command(name = "convert", description = "converts OWL to obographs JSON")
public class Convert implements Callable<Integer> {

    // obographs convert *.owl -o .
    // obographs convert owl/*.owl -o obographs/. -f all
    // obographs convert *.owl -o . -f json
    @Parameters(arity = "1..*", paramLabel = "FILE", description = "OWL/OBO file(s) to process.")
    private List<Path> inputPaths;

    @Option(names = {"-o", "--out-dir"}, description = "Output directory for converted files (defaults to input directory).")
    Path outDir;

    @Option(names = {"-f", "--format"}, paramLabel = "<format>", description = {
            "Output format for obograph files.",
            "The format parameter is optional (defaults to `all`), and is used to specify the output format(s) of converted files.",
            "The possible options are:",
            " * @|yellow json|@ - Write to JSON.",
            " * @|yellow yaml|@ - Write to YAML.",
            " * @|yellow all|@ - Write both JSON and YAML."
    })
    Format outFormat = Format.all;

    enum Format {json, yaml, all}

    @Override
    public Integer call() {
        if (outDir != null) {
            try {
                createDirectoryIfNotPresent(outDir);
            } catch (IOException ex) {
                System.err.println("Output " + outDir + " must be a writeable directory!");
                return 1;
            }
        }
        List<Format> formats = parseFormats(outFormat);
        OWLOntologyManager owlOntologyManager = OWLManager.createOWLOntologyManager();
        FromOwl fromOwl = new FromOwl();
        int exitCode = 0;
        for (Path inputFile : inputPaths) {
            try {
                System.err.println("Reading " + inputFile);
                OWLOntology owlOntology = owlOntologyManager.loadOntologyFromOntologyDocument(inputFile.toFile());
                GraphDocument graphDocument = fromOwl.generateGraphDocument(owlOntology);
                for (Format format : formats) {
                    Path outFile = outFile(inputFile, format);
                    if (format == Format.json) {
                        OgJsonGenerator.write(outFile.toFile(), graphDocument);
                    }
                    if (format == Format.yaml) {
                        OgYamlGenerator.write(outFile.toFile(), graphDocument);
                    }
                    System.err.println("Written " + outFile.toAbsolutePath());
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                exitCode = 1;
            }
        }
        return exitCode;
    }

    private List<Format> parseFormats(Format format) {
        if (format == Format.json) {
            return List.of(Format.json);
        } else if (format == Format.yaml) {
            return List.of(Format.yaml);
        }
        return List.of(Format.json, Format.yaml);
    }

    private Path outFile(Path inputFile, Format format) {
        String outFileName = outFileName(inputFile, format);
        if (outDir == null) {
            return inputFile.toAbsolutePath().getParent().resolve(outFileName);
        }
        return outDir.resolve(outFileName);
    }

    private String outFileName(Path inputFile, Format format) {
        String fileName = inputFile.getFileName().toString();
        if (fileName.endsWith(".obo")) {
            return fileName.replace(".obo", "." + format);
        } else if (fileName.endsWith(".owl")) {
            return fileName.replace(".owl", "." + format);
        } else if (fileName.endsWith(".ttl")) {
            return fileName.replace(".ttl", "." + format);
        }
        throw new IllegalArgumentException("Input file " + inputFile + " must be in OWL, OBO or TTL format");
    }

    private Path createDirectoryIfNotPresent(Path dir) throws IOException {
        try {
            return Files.createDirectory(dir);
        } catch (FileAlreadyExistsException x) {
            if (!Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS)) {
                throw new NotDirectoryException("Not a directory: " + dir);
            }
        }
        return dir;
    }
}
