package org.geneontology.obographs.model.meta;

import java.util.List;

import org.geneontology.obographs.model.Meta;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A generic {@link PropertyValue} that is not explicitly modeled
 * 
 * @author cjm
 *
 */
public class BasicPropertyValue extends AbstractPropertyValue implements PropertyValue {
	
	private BasicPropertyValue(Builder builder) {
        super(builder);
	}


	public static class Builder extends AbstractPropertyValue.Builder {

        public Builder pred(String pred) {
            return (Builder) super.pred(pred);
        }
        public Builder val(String val) {
            return (Builder) super.val(val);
        }
        public Builder xrefs(List<String> xrefs) {
            return (Builder) super.xrefs(xrefs);
        }
   

        public BasicPropertyValue build() {
        	return new BasicPropertyValue(this);
        }
    }
    
}
