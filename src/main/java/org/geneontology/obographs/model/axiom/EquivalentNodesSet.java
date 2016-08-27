package org.geneontology.obographs.model.axiom;

import java.util.Set;

import org.geneontology.obographs.model.Meta;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A set of nodes that all stand in a mutual equivalence or identity relationship to one another
 * 
 * Corresponds to Node in the OWLAPI
 * 
 * 
 * @author cjm
 *
 */
public class EquivalentNodesSet extends AbstractAxiom {

    private EquivalentNodesSet(Builder builder) {
        super(builder);
        representativeNodeId = builder.representativeNodeId;
        nodeIds = builder.nodeIds;
    }

    private final String representativeNodeId;
    private final Set<String> nodeIds;



    /**
     * @return the representativeNodeId
     */
    public String getRepresentativeNodeId() {
        return representativeNodeId;
    }



    /**
     * @return the nodeIds
     */
    public Set<String> getNodeIds() {
        return nodeIds;
    }



    public static class Builder extends AbstractAxiom.Builder {

        @JsonProperty
        private String representativeNodeId;
        @JsonProperty
        private Set<String> nodeIds;
        @JsonProperty
        private Meta meta;

        public Builder representativeNodeId(String representativeNodeId) {
            this.representativeNodeId = representativeNodeId;
            return this;
        }

        public Builder nodeIds(Set<String> nodeIds) {
            this.nodeIds = nodeIds;
            return this;
        }
        
        public EquivalentNodesSet build() {
            return new EquivalentNodesSet(this);
        }
    }


}
