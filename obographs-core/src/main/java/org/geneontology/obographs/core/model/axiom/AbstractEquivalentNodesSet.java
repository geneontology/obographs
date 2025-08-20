package org.geneontology.obographs.core.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;

import java.util.Comparator;
import java.util.Set;

/**
 * A set of nodes that all stand in a mutual equivalence or identity relationship to one another
 * <p>
 * Corresponds to Node in the OWLAPI
 * 
 * 
 * @author cjm
 *
 */
@JsonPropertyOrder({"representativeNodeId", "nodeIds", "meta"})
@Value.Immutable
public abstract class AbstractEquivalentNodesSet implements Axiom, Comparable<AbstractEquivalentNodesSet> {

    private static final Comparator<AbstractEquivalentNodesSet> COMPARATOR =
            Comparator.comparing(AbstractEquivalentNodesSet::representativeNodeId);

    /**
     * @return the representativeNodeId
     */
    @JsonProperty
    @Value.Default
    public String representativeNodeId() {
        String representative = nodeIds().iterator().next();
        return representative == null ? "" : representative;
    }

    /**
     * @return the nodeIds
     */
    @JsonProperty
//    @Value.NaturalOrder
    public abstract Set<String> nodeIds();

    @Override
    public int compareTo(AbstractEquivalentNodesSet o) {
        return COMPARATOR.compare(this, o);
    }
}
