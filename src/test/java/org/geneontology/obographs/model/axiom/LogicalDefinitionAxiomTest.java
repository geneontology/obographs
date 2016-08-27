package org.geneontology.obographs.model.axiom;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geneontology.obographs.io.OgJsonGenerator;
import org.geneontology.obographs.io.OgYamlGenerator;
import org.geneontology.obographs.model.Edge;
import org.geneontology.obographs.model.EdgeTest;
import org.geneontology.obographs.model.Graph;
import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.model.GraphDocumentTest;
import org.geneontology.obographs.model.GraphTest;
import org.geneontology.obographs.model.Node;
import org.geneontology.obographs.model.NodeTest;
import org.geneontology.obographs.model.axiom.EquivalentNodesSet;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class LogicalDefinitionAxiomTest {

    private static final String DC = "A:1";
    
    @Test
    public void test() throws JsonProcessingException {
        LogicalDefinitionAxiom lda = build();
        assertEquals(DC, lda.getDefinedClassId());
    }
    
 
    
    public static LogicalDefinitionAxiom build() {
        String[] ids = {"X:1", "X:2"};
        Set<String> nodeIds = new HashSet<>(
                Arrays.asList(ids));
        Set<ExistentialRestrictionExpression> rs = 
                Collections.singleton(ExistentialRestrictionAxiomTest.build());
        return new LogicalDefinitionAxiom.Builder().definedClassId(DC).genusIds(nodeIds).restrictions(rs).build();
        
    }


}
