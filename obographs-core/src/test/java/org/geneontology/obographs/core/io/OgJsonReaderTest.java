package org.geneontology.obographs.core.io;

import org.geneontology.obographs.core.model.GraphDocument;
import org.geneontology.obographs.core.model.Node;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class OgJsonReaderTest {

    @Test
    public void readFile() throws IOException {
        Instant start = Instant.now();
        Path ontologyPath = Paths.get("src/test/resources/hp.json");
        GraphDocument graphDocument = OgJsonReader.readFile(ontologyPath.toFile());
        Instant end = Instant.now();
        System.out.printf("Read %s in %dms%n", ontologyPath, Duration.between(start, end).toMillis());
        Node node = graphDocument.getGraphs().get(0).getNodes().get(0);
        System.out.println(node);
        System.out.println(OgJsonGenerator.render(node));
    }

    @Test
    public void readInputStream() throws IOException {
        Instant start = Instant.now();
        Path ontologyPath = Paths.get("src/test/resources/hp.json");
        GraphDocument graphDocument = OgJsonReader.readInputStream(Files.newInputStream(ontologyPath));
        Instant end = Instant.now();
        System.out.printf("Read %s in %dms%n", ontologyPath, Duration.between(start, end).toMillis());
        Node node = graphDocument.getGraphs().get(0).getNodes().get(0);
        System.out.println(node);
        System.out.println(OgJsonGenerator.render(node));
    }

    @Test
    public void readReader() throws IOException {
        Instant start = Instant.now();
        Path ontologyPath = Paths.get("src/test/resources/hp.json");
        GraphDocument graphDocument = OgJsonReader.read(Files.newBufferedReader(ontologyPath, StandardCharsets.UTF_8));
        Instant end = Instant.now();
        System.out.printf("Read %s in %dms%n", ontologyPath, Duration.between(start, end).toMillis());
        Node node = graphDocument.getGraphs().get(0).getNodes().get(0);
        System.out.println(node);
        System.out.println(OgJsonGenerator.render(node));
    }
}