package org.geneontology.obographs.model.axiom;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class DomainRangeAxiomTest {

    static String pred = "part_of";
    static String domain = "X:1";
    static String range = "X:1";
    
    @Test
    public void test() {
        DomainRangeAxiom a = build();
        assertEquals(pred, a.getPredicateId());
    }

    
    public static DomainRangeAxiom build() {
        return new DomainRangeAxiom.Builder().predicateId(pred).domainClassId(domain).rangeClassId(range).build();
        
    }
}
