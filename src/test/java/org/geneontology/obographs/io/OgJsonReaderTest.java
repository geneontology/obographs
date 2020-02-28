package org.geneontology.obographs.io;

import org.geneontology.obographs.model.GraphDocument;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class OgJsonReaderTest {

    @Test
    public void readFile() throws IOException {
        Path ontologyPath = Paths.get("src/test/resources/hp.json");
        GraphDocument graphDocument = OgJsonReader.readFile(ontologyPath.toFile());
        System.out.println(graphDocument.getGraphs().get(0).getNodes().get(0));
    }
}