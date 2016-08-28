package org.geneontology.obographs.model;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.meta.DefinitionPropertyValue;
import org.geneontology.obographs.model.meta.SynonymPropertyValue;
import org.geneontology.obographs.model.meta.SynonymPropertyValueTest;
import org.geneontology.obographs.model.meta.XrefPropertyValue;
import org.junit.Test;

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
        assertEquals(xrefVal, m.getXrefs().get(0).getVal());   
    }

    public static Meta build() {

        SynonymPropertyValue s = SynonymPropertyValueTest.build();
        DefinitionPropertyValue def = new DefinitionPropertyValue.
                Builder().
                val(defval).
                xrefs(Arrays.asList(defXrefs)).
                build();
        XrefPropertyValue xref = new XrefPropertyValue.
                Builder().
                val(xrefVal).
                //xrefs(Arrays.asList(defXrefs)).
                build();
        return new Meta.Builder().
                definition(def).
                synonyms(Collections.singletonList(s)).
                xrefs(Collections.singletonList(xref)).
                subsets(subsets).build();
    }

}
