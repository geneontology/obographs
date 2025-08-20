package org.geneontology.obographs.core.model.axiom;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PropertyChainAxiomTest {

    static String pred = "overlaps";
    static String pc1 = "has_part";
    static String pc2 = "part_of";
    
    @Test
    void test() {
        PropertyChainAxiom a = build();
        assertEquals(pred, a.predicateId());
        assertEquals(pc1, a.chainPredicateIds().get(0));
        assertEquals(pc2, a.chainPredicateIds().get(1));
    }

    
    public static PropertyChainAxiom build() {
        List<String> pc = List.of(pc1, pc2);
        return new PropertyChainAxiom.Builder().predicateId(pred).chainPredicateIds(pc).build();
        
    }
}
