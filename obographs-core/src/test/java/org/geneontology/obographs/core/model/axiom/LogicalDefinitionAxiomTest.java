package org.geneontology.obographs.core.model.axiom;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogicalDefinitionAxiomTest {

    private static final String DC = "A:1";
    
    @Test
    void test() {
        LogicalDefinitionAxiom lda = build();
        assertEquals(DC, lda.definedClassId());
    }
    
    public static LogicalDefinitionAxiom build() {
        List<String> nodeIds = List.of("X:1", "X:2");
        List<ExistentialRestrictionExpression> rs = List.of(ExistentialRestrictionAxiomTest.build());
        return new LogicalDefinitionAxiom.Builder().definedClassId(DC).genusIds(nodeIds).restrictions(rs).build();
    }


}
