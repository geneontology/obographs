package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;

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
@JsonPropertyOrder({"representativeNodeId", "nodeIds", "meta"})
@Value.Immutable
public abstract class AbstractEquivalentNodesSet implements Axiom {

    /**
     * @return the representativeNodeId
     */
    @JsonProperty
    @Value.Default
    public String getRepresentativeNodeId() {
        String representative = getNodeIds().iterator().next();
        return representative == null ? "" : representative;
    }

    /**
     * @return the nodeIds
     */
    @JsonProperty
    @Value.NaturalOrder
    public abstract SortedSet<String> getNodeIds();

}
