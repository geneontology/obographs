package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class EquivalentNodesSetTest {

    @Test
    public void test() throws JsonProcessingException {
        EquivalentNodesSet ens = build();
        assertEquals(2, ens.getNodeIds().size());
    }



    public static EquivalentNodesSet build() {
        String[] ids = {"X:1", "X:2"};
        Set<String> nodeIds = new HashSet<>(Arrays.asList(ids));
        return new EquivalentNodesSet.Builder().nodeIds(nodeIds).representativeNodeId(ids[0]).build();
    }

}
