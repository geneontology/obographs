package org.geneontology.obographs.model.meta;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.geneontology.obographs.model.meta.SynonymPropertyValue.SCOPES;
import org.junit.Test;

public class SynonymPropertyValueTest {

    static String[] synXrefs = {"GOC:go_curators"};
    static String val = "cell nucleus";
    static SCOPES scope = SynonymPropertyValue.SCOPES.EXACT;
    static String synonymType = "special_synonym";

    @Test
    public void test() {
        SynonymPropertyValue spv = build();
        testSyn(spv);
    }

    public void testSyn(SynonymPropertyValue spv) {
        assertEquals(synonymType, spv.getSynonymType());
        assertEquals(val, spv.getVal());
        assertTrue(spv.isExact());
        assertEquals(1, spv.getXrefs().size());
        assertEquals(synXrefs[0], spv.getXrefs().get(0));
    }

    public static SynonymPropertyValue build() {
        return new SynonymPropertyValue.Builder().val(val).
                scope(scope).
                synonymType(synonymType).
                xrefs(Arrays.asList(synXrefs)).
                build();
    }

}
