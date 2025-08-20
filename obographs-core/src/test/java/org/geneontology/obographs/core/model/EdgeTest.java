package org.geneontology.obographs.core.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EdgeTest {

	static String pred = "part_of";

	@Test
	void test() {
		Edge e = build();
		assertEquals(NodeTest.id, e.sub());
		assertEquals(pred, e.pred());
		assertEquals(NodeTest.parent_id, e.obj());
	}
	
	public static Edge build() {
		return new Edge.Builder().sub(NodeTest.id).pred(pred).obj(NodeTest.parent_id).build();
	}

}
