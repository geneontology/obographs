package org.geneontology.obographs.core.model.axiom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DomainRangeAxiomTest {

    static String pred = "part_of";
    static String domain = "X:1";
    static String range = "X:1";
    
    @Test
    void test() {
        DomainRangeAxiom a = build();
        assertEquals(pred, a.predicateId());
    }

    public static DomainRangeAxiom build() {
        return new DomainRangeAxiom.Builder()
                .predicateId(pred)
                .addDomainClassId(domain)
                .addRangeClassId(range)
                .build();
    }
}
