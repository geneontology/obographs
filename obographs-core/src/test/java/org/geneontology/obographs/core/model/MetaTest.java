package org.geneontology.obographs.core.model;

import org.geneontology.obographs.core.model.meta.*;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        assertEquals(defval, m.definition().val());
        assertEquals(1, m.definition().xrefs().size());
        assertEquals(2, m.subsets().size());
        assertEquals(1, m.xrefs().size());
        assertEquals(XrefPropertyValueTest.val, m.xrefs().get(0).val());
        assertEquals(XrefPropertyValueTest.lbl, m.xrefs().get(0).lbl());
        assertFalse(m.isDeprecated());
    }

    public static Meta build() {

        SynonymPropertyValue s = SynonymPropertyValueTest.build();
        XrefPropertyValue xref = XrefPropertyValueTest.build();
        DefinitionPropertyValue def = new DefinitionPropertyValue.
                Builder().
                val(defval).
                xrefs(List.of(defXrefs)).
                build();

        return new Meta.Builder().
                definition(def).
                synonyms(List.of(s)).
                xrefs(List.of(xref)).
                subsets(List.of(subsets))
                .deprecated(false)
                .build();
    }

}
