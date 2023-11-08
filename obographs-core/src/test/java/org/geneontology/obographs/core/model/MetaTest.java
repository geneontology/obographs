package org.geneontology.obographs.core.model;

import org.geneontology.obographs.core.model.meta.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MetaTest {

    static String defval = "A membrane-bounded organelle of eukaryotic cells in which chromosomes are housed and replicated. In most cells, the nucleus contains all of the cell's chromosomes except the organellar chromosomes, and is the site of RNA synthesis and processing. In some species, or in specialized cell types, RNA metabolism or DNA replication may be absent.";
    static String[] defXrefs = {"GOC:go_curators"};
    static String[] subsets = {"goslim_yeast", "goslim_plant"};
    static String xrefVal = "Wikipedia:Cell_nucleus";

    @Test
    public void test() {
        Meta m = build();
        testMeta(m);
    }

    public static void testMeta(Meta m) {
        assertEquals(defval, m.getDefinition().getVal());       
        assertEquals(1, m.getDefinition().getXrefs().size());       
        assertEquals(2, m.getSubsets().size());   
        assertEquals(1, m.getXrefs().size());   
        assertEquals(XrefPropertyValueTest.val, m.getXrefs().get(0).getVal());
        assertEquals(XrefPropertyValueTest.lbl, m.getXrefs().get(0).getLbl());
        assertFalse(m.getDeprecated());
    }

    public static Meta build() {

        SynonymPropertyValue s = SynonymPropertyValueTest.build();
        XrefPropertyValue xref = XrefPropertyValueTest.build();
        DefinitionPropertyValue def = new DefinitionPropertyValue.
                Builder().
                val(defval).
                xrefs(Arrays.asList(defXrefs)).
                build();

        return new Meta.Builder().
                definition(def).
                synonyms(Collections.singletonList(s)).
                xrefs(Collections.singletonList(xref)).
                subsets(Arrays.asList(subsets))
                .deprecated(false)
                .build();
    }

}
