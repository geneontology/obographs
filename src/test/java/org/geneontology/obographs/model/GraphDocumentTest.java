package org.geneontology.obographs.model;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geneontology.obographs.io.OgJsonGenerator;
import org.geneontology.obographs.io.OgYamlGenerator;
import org.geneontology.obographs.model.Graph;
import org.geneontology.obographs.model.GraphDocument;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GraphDocumentTest {

    @Test
    public void test() throws JsonProcessingException {
        GraphDocument d = build();
        Graph g = d.getGraphs().get(0);
        assertEquals(1, g.getNodes().size());
        assertEquals(1, g.getEdges().size());
        assertEquals(1, d.getGraphs().size());

        System.out.println(OgJsonGenerator.render(d));
        System.out.println(OgYamlGenerator.render(d));
    }
    
    public static GraphDocument build() {
        Graph g = GraphTest.build();
        List<Graph> graphs =  (List<Graph>) Collections.singletonList(g);

        Map<Object,Object> context = new HashMap<>();
        context.put("GO", "http://purl.obolibrary.org/obo/GO_");

        GraphDocument d = new GraphDocument.Builder().
                context(context).
                graphs(graphs).build();
        return d;
    }



}
