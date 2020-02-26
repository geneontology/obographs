package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * P <- P1 ... Pn
 * 
 * 
 * @author cjm
 *
 */
@JsonDeserialize(builder = PropertyChainAxiom.Builder.class)
public class PropertyChainAxiom extends AbstractAxiom {

    private PropertyChainAxiom(Builder builder) {
        super(builder);
        predicateId = builder.predicateId;
        chainPredicateIds = builder.chainPredicateIds;
        
    }

    private final String predicateId;
    private final List<String> chainPredicateIds;
    
    /**
     * @return the predicateId
     */
    public String getPredicateId() {
        return predicateId;
    }
    
    /**
     * @return the chainPredicateIds
     */
    public List<String> getChainPredicateIds() {
        return chainPredicateIds;
    }


  

    public static class Builder extends AbstractAxiom.Builder {

        @JsonProperty
        private String predicateId;
        @JsonProperty
        private List<String> chainPredicateIds;
 
        public Builder predicateId(String predicateId) {
            this.predicateId = predicateId;
            return this;
        }
 
        public Builder chainPredicateIds(List<String> chainPredicateIds) {
            this.chainPredicateIds = chainPredicateIds;
            return this;
        }

        @JsonCreator
        public PropertyChainAxiom build() {
            return new PropertyChainAxiom(this);
        }

  
    }


}
