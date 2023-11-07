package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.geneontology.obographs.core.io.OgJsonGenerator;
import org.geneontology.obographs.core.io.OgYamlGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphTest {

	@Test
	public void test() throws JsonProcessingException {
	    Graph g = build();
		assertEquals(2, g.getNodes().size());
		assertEquals(1, g.getEdges().size());
		
		System.out.println(OgJsonGenerator.render(g));
		System.out.println(OgYamlGenerator.render(g));
	}
	
	public static Graph build() {
        Node n = NodeTest.build();
        Node p = NodeTest.buildParent();
        Edge e = EdgeTest.build();
        
        List<Node> nodes = new ArrayList<>();
        nodes.add(n);
        nodes.add(p);
        List<Edge> edges = (List<Edge>) Collections.singletonList(e);
        Graph g = new Graph.Builder().nodes(nodes).edges(edges).build();
	    return g;
	}
	
	

}
