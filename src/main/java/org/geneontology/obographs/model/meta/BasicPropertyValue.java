package org.geneontology.obographs.model.meta;

import org.geneontology.obographs.model.Meta;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicPropertyValue extends AbstractPropertyValue implements PropertyValue {
	
	private BasicPropertyValue(Builder builder) {
        super(builder);
	}


	public static class Builder extends AbstractPropertyValue.Builder {

    

        public BasicPropertyValue build() {
        	return new BasicPropertyValue(this);
        }
    }
    
}
