package org.geneontology.obographs.model;

import static org.junit.Assert.*;

import org.geneontology.obographs.model.Edge;
import org.junit.Test;

public class EdgeTest {

	static String subj = "X:1";
	static String pred = "part-of";
	static String obj = "X:2";

	@Test
	public void test() {
		Edge e = build();
		assertEquals(subj, e.getSubj());
		assertEquals(pred, e.getPred());
		assertEquals(obj, e.getObj());
	}
	
	public static Edge build() {
		return new Edge.Builder().subj(subj).pred(pred).obj(obj).build();
	}

}
