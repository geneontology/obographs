package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;


/**
 * A {@link PropertyValue} that represents a textual definition of an ontology class or
 * property
 *
 * @author cjm
 */
@JsonDeserialize(builder = DefinitionPropertyValue.Builder.class)
public class DefinitionPropertyValue extends AbstractPropertyValue {

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

        @JsonCreator
        public DefinitionPropertyValue build() {
            return new DefinitionPropertyValue(this);
        }
    }

}
