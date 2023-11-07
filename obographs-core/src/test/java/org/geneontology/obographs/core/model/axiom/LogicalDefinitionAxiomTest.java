package org.geneontology.obographs.core.model.axiom;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogicalDefinitionAxiomTest {

    private static final String DC = "A:1";
    
    @Test
    public void test() throws JsonProcessingException {
        LogicalDefinitionAxiom lda = build();
        assertEquals(DC, lda.getDefinedClassId());
    }
    
    public static LogicalDefinitionAxiom build() {
        String[] ids = {"X:1", "X:2"};
        List<String> nodeIds = new ArrayList<>(
                Arrays.asList(ids));
        List<ExistentialRestrictionExpression> rs = 
                Collections.singletonList(ExistentialRestrictionAxiomTest.build());
        return new LogicalDefinitionAxiom.Builder().definedClassId(DC).genusIds(nodeIds).restrictions(rs).build();
        
    }


}
