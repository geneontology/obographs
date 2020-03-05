package org.geneontology.obographs.model.axiom;

import org.geneontology.obographs.model.Meta;

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

        private Meta meta;

        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
        }

      
    } 
    
}
