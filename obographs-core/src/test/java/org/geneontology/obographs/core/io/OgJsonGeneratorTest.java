package org.geneontology.obographs.core.io;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.core.model.*;
import org.geneontology.obographs.core.model.meta.SynonymPropertyValue;
import org.geneontology.obographs.core.model.meta.XrefPropertyValue;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OgJsonGeneratorTest {

    @Test
    public void test() throws IOException {
        GraphDocument d = GraphDocumentTest.build();
  
        String s = OgJsonGenerator.render(d);
        System.out.println(s);
        FileUtils.writeStringToFile(new File("target/simple-example.json"), s);
    }

    @Test
    public void testRead() throws IOException {
        GraphDocument testGraphDocument = GraphDocumentTest.build();

        Path tempFile = Files.createTempFile("simple-example", ".json");
        FileUtils.writeStringToFile(tempFile.toFile(), OgJsonGenerator.render(testGraphDocument));

        GraphDocument graphDocument = OgJsonReader.readFile(tempFile.toFile());

        assertEquals(testGraphDocument, graphDocument);

        assertEquals(1, graphDocument.graphs().size());
        Graph graph = graphDocument.graphs().get(0);
        assertEquals(2, graph.nodes().size());

        Node node1 = graph.nodes().get(0);
        assertEquals("GO:0005634", node1.id());
        assertEquals("nucleus", node1.label());
        Meta node1Meta = node1.meta();
        assertEquals("A membrane-bounded organelle of eukaryotic cells in which chromosomes are housed and " +
                "replicated. In most cells, the nucleus contains all of the cell's chromosomes except the organellar " +
                "chromosomes, and is the site of RNA synthesis and processing. In some species, or in specialized cell" +
                " types, RNA metabolism or DNA replication may be absent.", node1Meta.definition().val());
        assertEquals(Arrays.asList("GOC:go_curators"), node1Meta.definition().xrefs());
        assertEquals(Arrays.asList("goslim_yeast", "goslim_plant"), node1Meta.subsets());

        XrefPropertyValue xrefPropertyValue = node1Meta.xrefs().get(0);
        assertEquals("ICD10:111", xrefPropertyValue.val());
        assertEquals("foo disease", xrefPropertyValue.lbl());

        SynonymPropertyValue synonymPropertyValue = node1Meta.synonyms().get(0);
        assertEquals("cell nucleus", synonymPropertyValue.val());
        assertEquals("hasExactSynonym", synonymPropertyValue.pred());
        assertEquals(Arrays.asList("GOC:go_curators"), synonymPropertyValue.xrefs());

        Node node2 = graph.nodes().get(1);
        assertEquals("GO:0005623", node2.id());
        assertEquals("cell", node2.label());

        assertEquals(1, graph.edges().size());
        Edge edge = graph.edges().get(0);
        assertEquals("GO:0005634", edge.sub());
        assertEquals("part_of", edge.pred());
        assertEquals("GO:0005623", edge.obj());
    }

}
