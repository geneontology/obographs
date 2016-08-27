package org.geneontology.obographs.model;

import static org.junit.Assert.*;

import org.geneontology.obographs.model.Meta;
import org.junit.Test;

public class MetaTest {

	static String pred = "comment";
	static String val = "hi";

	@Test
	public void test() {
		Meta m = build();
		assertEquals(pred, m.getPred());
		assertEquals(val, m.getVal());
	}
	
	public static Meta build() {
		return new Meta.Builder().pred(pred).val(val).build();
	}

}
