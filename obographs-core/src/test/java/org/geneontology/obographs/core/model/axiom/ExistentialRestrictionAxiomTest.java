package org.geneontology.obographs.core.model.axiom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExistentialRestrictionAxiomTest {

    private static final String REL = "part_of";
    private static final String FILLER = "Z:1";

    @Test
    void test() {
        ExistentialRestrictionExpression r = build();
        assertEquals(REL, r.propertyId());
        assertEquals(FILLER, r.fillerId());
    }

    public static ExistentialRestrictionExpression build() {
        return new ExistentialRestrictionExpression.Builder().propertyId(REL).fillerId(FILLER).build();
    }


}
