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

public class ExistentialRestrictionAxiomTest {

    private static final String REL = "part_of";
    private static final String FILLER = "Z:1";


    @Test
    public void test() throws JsonProcessingException {
        ExistentialRestrictionExpression r = build();
        assertEquals(REL, r.getPropertyId());
        assertEquals(FILLER, r.getFillerId());
    }



    public static ExistentialRestrictionExpression build() {

        return new ExistentialRestrictionExpression.Builder().propertyId(REL).fillerId(FILLER).build();

    }


}
