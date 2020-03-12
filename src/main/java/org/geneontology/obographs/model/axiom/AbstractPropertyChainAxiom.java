package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
public abstract class AbstractPropertyChainAxiom implements Axiom {

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

}
