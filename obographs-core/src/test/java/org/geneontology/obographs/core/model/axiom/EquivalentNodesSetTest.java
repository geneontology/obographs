package org.geneontology.obographs.core.model.axiom;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EquivalentNodesSetTest {

    @Test
    void test() {
        EquivalentNodesSet ens = build();
        assertEquals(2, ens.nodeIds().size());
    }

    public static EquivalentNodesSet build() {
        String[] ids = {"X:1", "X:2"};
        Set<String> nodeIds = Set.of(ids);
        return new EquivalentNodesSet.Builder().nodeIds(nodeIds).representativeNodeId(ids[0]).build();
    }

}
