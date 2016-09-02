package org.geneontology.obographs.model;

import static org.junit.Assert.*;

import org.geneontology.obographs.model.Edge;
import org.junit.Test;

public class EdgeTest {

	static String pred = "part_of";

	@Test
	public void test() {
		Edge e = build();
		assertEquals(NodeTest.id, e.getSub());
		assertEquals(pred, e.getPred());
		assertEquals(NodeTest.parent_id, e.getObj());
	}
	
	public static Edge build() {
		return new Edge.Builder().sub(NodeTest.id).pred(pred).obj(NodeTest.parent_id).build();
	}

}
