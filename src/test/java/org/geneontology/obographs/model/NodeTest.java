package org.geneontology.obographs.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NodeTest {

    static String id = "GO:0005634";
    static String lbl = "nucleus";
    
    static String parent_id = "GO:0005623";
    static String parent_lbl = "cell";

	@Test
	public void test() {
        Node n = build();
        Node p = buildParent();
        assertEquals(id, n.getId());
        assertEquals(lbl, n.getLabel());
        assertEquals(parent_id, p.getId());
        assertEquals(parent_lbl, p.getLabel());
		
		Meta m = MetaTest.build();
		MetaTest.testMeta(m);
		assertNull(p.getMeta());
	}
	
    public static Node build() {
        Meta m = MetaTest.build();
        return new Node.Builder().id(id).label(lbl).meta(m).build();
    }
    
    public static Node buildParent() {
        Meta m = MetaTest.build();
        return new Node.Builder().id(parent_id).label(parent_lbl).build();
    }

}
