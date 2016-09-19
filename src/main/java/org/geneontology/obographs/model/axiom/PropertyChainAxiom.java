package org.geneontology.obographs.model.axiom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geneontology.obographs.model.Edge;
import org.geneontology.obographs.model.Meta;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * P <- P1 ... Pn
 * 
 * 
 * @author cjm
 *
 */
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
        
        public PropertyChainAxiom build() {
            return new PropertyChainAxiom(this);
        }

  
    }


}
