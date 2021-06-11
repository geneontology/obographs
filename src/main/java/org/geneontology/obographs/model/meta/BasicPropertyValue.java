package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * A generic {@link PropertyValue} that is not explicitly modeled
 *
 * @author cjm
 */
@JsonDeserialize(builder = BasicPropertyValue.Builder.class)
public class BasicPropertyValue extends AbstractPropertyValue {

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

        @JsonCreator
        public BasicPropertyValue build() {
            return new BasicPropertyValue(this);
        }
    }

}
