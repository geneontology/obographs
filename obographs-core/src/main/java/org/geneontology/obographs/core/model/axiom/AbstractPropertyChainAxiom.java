package org.geneontology.obographs.core.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ComparisonChain;
import org.immutables.value.Value;

import java.util.List;

/**
 * P <- P1 ... Pn
 * 
 * 
 * @author cjm
 *
 */
@JsonPropertyOrder({"predicateId", "chainPredicateIds", "meta"})
@Value.Immutable
public abstract class AbstractPropertyChainAxiom implements Axiom, Comparable<AbstractPropertyChainAxiom> {

    /**
     * @return the predicateId
     */
    @JsonProperty
    public abstract String getPredicateId();
    
    /**
     * @return the chainPredicateIds
     */
    @JsonProperty
    public abstract List<String> getChainPredicateIds();

    @Override
    public int compareTo(AbstractPropertyChainAxiom o) {
        return ComparisonChain.start()
                .compare(this.getPredicateId(), o.getPredicateId())
                .result();
    }
}
