package org.geneontology.obographs.core.model.meta;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XrefPropertyValueTest {

    public static String val = "ICD10:111";
    public static String lbl = "foo disease";
    
    @Test
    void test() {
        XrefPropertyValue spv = build();
        assertEquals(val, spv.val());
        assertEquals(lbl, spv.lbl());
    }

    public static XrefPropertyValue build() {
        return new XrefPropertyValue.Builder()
                .val(val)
                .lbl(lbl)
                .build();
    }

}
