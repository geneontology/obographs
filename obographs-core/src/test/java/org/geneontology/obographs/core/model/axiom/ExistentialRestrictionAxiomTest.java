package org.geneontology.obographs.core.model.axiom;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExistentialRestrictionAxiomTest {

    private static final String REL = "part_of";
    private static final String FILLER = "Z:1";


    @Test
    public void test() throws JsonProcessingException {
        ExistentialRestrictionExpression r = build();
        assertEquals(REL, r.getPropertyId());
        assertEquals(FILLER, r.getFillerId());
    }



    public static ExistentialRestrictionExpression build() {

        return new ExistentialRestrictionExpression.Builder().propertyId(REL).fillerId(FILLER).build();

    }


}
