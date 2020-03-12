package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * P <- P1 ... Pn
 * 
 * 
 * @author cjm
 *
 */
@JsonSerialize(as = PropertyChainAxiom.class)
@JsonDeserialize(as = PropertyChainAxiom.class)
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
