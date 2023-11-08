package org.geneontology.obographs.core.model.axiom;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.geneontology.obographs.core.io.OgJsonGenerator;
import org.geneontology.obographs.core.io.OgYamlGenerator;
import org.geneontology.obographs.core.model.*;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdvancedAxiomsTest {

    @Test
    public void test() throws JsonProcessingException {
        GraphDocument d = buildGD();
        
        Graph g = d.getGraphs().get(0);
        assertEquals(1,g.getLogicalDefinitionAxioms().size());
 
        System.out.println(OgJsonGenerator.render(d));
        System.out.println(OgYamlGenerator.render(d));
    }
    
    public static GraphDocument buildGD() {
        Graph g = buildGraph();
        List<Graph> graphs =  (List<Graph>) Collections.singletonList(g);

        Map<Object,Object> context = new HashMap<>();
        context.put("GO", "http://purl.obolibrary.org/obo/GO_");

        GraphDocument d = new GraphDocument.Builder().
                context(context).
                graphs(graphs).build();
        return d;
    }



    public static Graph buildGraph() {
        Node n = NodeTest.build();
        Edge e = EdgeTest.build();
        
        List<Node> nodes = Collections.singletonList(n);
        List<Edge> edges = Collections.singletonList(e);
        List<EquivalentNodesSet> enss = Collections.singletonList(EquivalentNodesSetTest.build());
        List<LogicalDefinitionAxiom> ldas = Collections.singletonList(LogicalDefinitionAxiomTest.build());
        Graph g = new Graph.Builder().nodes(nodes ).edges(edges).equivalentNodesSets(enss).logicalDefinitionAxioms(ldas).build();
        return g;
    }

}
