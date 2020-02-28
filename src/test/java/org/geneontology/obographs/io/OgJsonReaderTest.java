package org.geneontology.obographs.io;

import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.model.Node;
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
        Node node = graphDocument.getGraphs().get(0).getNodes().get(0);
        System.out.println(node);
        System.out.println(OgJsonGenerator.render(node));
    }
}