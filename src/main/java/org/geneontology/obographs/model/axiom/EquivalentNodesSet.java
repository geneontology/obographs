package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableSortedSet;
import org.geneontology.obographs.model.Meta;

import java.util.Set;
import java.util.SortedSet;

/**
 * A set of nodes that all stand in a mutual equivalence or identity relationship to one another
 * 
 * Corresponds to Node in the OWLAPI
 * 
 * 
 * @author cjm
 *
 */
@JsonDeserialize(builder = EquivalentNodesSet.Builder.class)
public class EquivalentNodesSet extends AbstractAxiom {

    private EquivalentNodesSet(Builder builder) {
        super(builder);
        representativeNodeId = builder.representativeNodeId;
        nodeIds = builder.nodeIds;
    }

    private final String representativeNodeId;
    private final SortedSet<String> nodeIds;

    /**
     * @return the representativeNodeId
     */
    public String getRepresentativeNodeId() {
        return representativeNodeId;
    }


    /**
     * @return the nodeIds
     */
    public SortedSet<String> getNodeIds() {
        return nodeIds;
    }



    public static class Builder extends AbstractAxiom.Builder {

        @JsonProperty
        private String representativeNodeId;
        @JsonProperty
        private SortedSet<String> nodeIds;
        private Meta meta;

        public Builder representativeNodeId(String representativeNodeId) {
            this.representativeNodeId = representativeNodeId;
            return this;
        }

        public Builder nodeIds(Set<String> nodeIds) {
            this.nodeIds = ImmutableSortedSet.copyOf(nodeIds);
            this.representativeNodeId = this.nodeIds.first();
            return this;
        }

        @JsonCreator
        public EquivalentNodesSet build() {
            return new EquivalentNodesSet(this);
        }
    }


}
