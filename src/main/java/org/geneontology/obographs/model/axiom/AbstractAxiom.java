package org.geneontology.obographs.model.axiom;

import java.util.Set;

import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.axiom.EquivalentNodesSet.Builder;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AbstractAxiom implements Axiom {

    protected AbstractAxiom(Builder builder) {
        meta = builder.meta;
    }

    protected final Meta meta;

  
    
    

    /**
     * @return the meta
     */
    public Meta getMeta() {
        return meta;
    }

    
    public static class Builder {

        @JsonProperty
        private Meta meta;
       
        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
        }

      
    } 
    
}
