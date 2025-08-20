package org.geneontology.obographs.core.model.meta;

import org.geneontology.obographs.core.model.meta.AbstractSynonymPropertyValue.Scope;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SynonymPropertyValueTest {

    static String[] synXrefs = {"GOC:go_curators"};
    static String val = "cell nucleus";
    static Scope scope = Scope.EXACT;
    static String synonymType = "special_synonym";

    @Test
    void test() {
        SynonymPropertyValue spv = build();
        assertEquals(synonymType, spv.synonymType());
        assertEquals(val, spv.val());
        assertTrue(spv.isExact());
        assertEquals(1, spv.xrefs().size());
        assertEquals(synXrefs[0], spv.xrefs().get(0));
    }

    public static SynonymPropertyValue build() {
        return new SynonymPropertyValue.Builder().val(val).
                pred(scope.pred()).
                synonymType(synonymType).
                xrefs(Arrays.asList(synXrefs)).
                build();
    }

}
