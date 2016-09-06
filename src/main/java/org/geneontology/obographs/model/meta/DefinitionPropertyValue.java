package org.geneontology.obographs.model.meta;

import java.util.List;

import org.geneontology.obographs.model.meta.AbstractPropertyValue.Builder;


/**
 * A {@link PropertyValue} that represents a textual definition of an ontology class or
 * property
 * 
 * @author cjm
 *
 */
public class DefinitionPropertyValue extends AbstractPropertyValue implements PropertyValue {
    
    private DefinitionPropertyValue(Builder builder) {
        super(builder);
    }


    public static class Builder extends AbstractPropertyValue.Builder {

        public Builder val(String val) {
            return (Builder) super.val(val);
        }
        public Builder xrefs(List<String> xrefs) {
            return (Builder) super.xrefs(xrefs);
        }
        
        public DefinitionPropertyValue build() {
            return new DefinitionPropertyValue(this);
        }
    }
    
}
