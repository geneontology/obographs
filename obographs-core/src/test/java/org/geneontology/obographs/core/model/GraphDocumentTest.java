package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.geneontology.obographs.core.io.OgJsonGenerator;
import org.geneontology.obographs.core.io.OgYamlGenerator;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphDocumentTest {

    @Test
    public void test() throws JsonProcessingException {
        GraphDocument d = build();
        Graph g = d.graphs().get(0);
        assertEquals(2, g.nodes().size());
        assertEquals(1, g.edges().size());
        assertEquals(1, d.graphs().size());

        System.out.println(OgJsonGenerator.render(d));
        System.out.println(OgYamlGenerator.render(d));
    }
    
    public static GraphDocument build() {
        Graph g = GraphTest.build();
        List<Graph> graphs = List.of(g);

        Map<Object,Object> context = Map.of("GO", "http://purl.obolibrary.org/obo/GO_");

        return new GraphDocument.Builder()
                .context(context)
                .graphs(graphs)
                .build();
    }



}
