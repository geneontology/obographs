package org.geneontology.obographs.core.model.axiom;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DomainRangeAxiomTest {

    static String pred = "part_of";
    static String domain = "X:1";
    static String range = "X:1";
    
    @Test
    public void test() {
        DomainRangeAxiom a = build();
        assertEquals(pred, a.getPredicateId());
    }

    public static DomainRangeAxiom build() {
        return new DomainRangeAxiom.Builder()
                .predicateId(pred)
                .addDomainClassId(domain)
                .addRangeClassId(range)
                .build();
    }
}
