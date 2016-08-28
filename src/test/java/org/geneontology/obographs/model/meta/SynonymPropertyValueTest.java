package org.geneontology.obographs.model.meta;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.geneontology.obographs.model.meta.SynonymPropertyValue.SCOPES;
import org.junit.Test;

public class SynonymPropertyValueTest {

    static String[] synXrefs = {"GOC:go_curators"};
    static String val = "cell nucleus";
    static SCOPES scope = SynonymPropertyValue.SCOPES.EXACT;

    @Test
    public void test() {
        SynonymPropertyValue spv = build();
        testSyn(spv);
    }

    public void testSyn(SynonymPropertyValue spv) {
        assertEquals(val, spv.getVal());
    }

    public static SynonymPropertyValue build() {
        return new SynonymPropertyValue.Builder().val(val).
                scope(scope).
                xrefs(Arrays.asList(synXrefs)).
                build();
    }

}
