package org.geneontology.obographs.core.model.axiom;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertyChainAxiomTest {

    static String pred = "overlaps";
    static String pc1 = "has_part";
    static String pc2 = "part_of";
    
    @Test
    public void test() {
        PropertyChainAxiom a = build();
        assertEquals(pred, a.getPredicateId());
        assertEquals(pc1, a.getChainPredicateIds().get(0));
        assertEquals(pc2, a.getChainPredicateIds().get(1));
    }

    
    public static PropertyChainAxiom build() {
        List<String> pc  = new ArrayList<>();
        pc.add(pc1);
        pc.add(pc2);
        return new PropertyChainAxiom.Builder().predicateId(pred).chainPredicateIds(pc).build();
        
    }
}
