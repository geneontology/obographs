package org.geneontology.obographs.model;

import static org.junit.Assert.*;

import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.Node;
import org.junit.Test;

public class NodeTest {

	static String id = "X:1";
	static String lbl = "foo";

	@Test
	public void test() {
		Node n = build();
		assertEquals(id, n.getId());
		assertEquals(lbl, n.getLabel());
		
		Meta m = MetaTest.build();
		assertEquals(m.getPred(), n.getMeta().getPred());
		assertEquals(m.getVal(), n.getMeta().getVal());
	}
	
	public static Node build() {
		Meta m = MetaTest.build();
		return new Node.Builder().id(id).label(lbl).meta(m).build();
	}

}
