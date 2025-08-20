package org.geneontology.obographs.core.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;

import java.util.Comparator;
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


    private static final Comparator<AbstractPropertyChainAxiom> COMPARATOR =
            Comparator.comparing(AbstractPropertyChainAxiom::predicateId);

    /**
     * @return the predicateId
     */
    @JsonProperty
    public abstract String predicateId();
    
    /**
     * @return the chainPredicateIds
     */
    @JsonProperty
    public abstract List<String> chainPredicateIds();

    @Override
    public int compareTo(AbstractPropertyChainAxiom o) {
        return COMPARATOR.compare(this, o);
    }
}
