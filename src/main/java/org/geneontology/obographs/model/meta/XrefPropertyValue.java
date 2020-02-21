package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;


@JsonDeserialize(builder = XrefPropertyValue.Builder.class)
public class XrefPropertyValue extends AbstractPropertyValue {

    private final String lbl;

    private XrefPropertyValue(Builder builder) {
        super(builder);
        lbl = builder.lbl;
    }

     
  
    /**
     * @return the lbl
     */
    public String getLbl() {
        return lbl;
    }



    public static class Builder extends AbstractPropertyValue.Builder {

        @JsonProperty
        private String lbl;
        
        @Override
        public Builder val(String val) {
            return (Builder) super.val(val);
        }

        public Builder lbl(String lbl) {
            this.lbl= lbl;
            return this;
        }

        @Override
        public Builder xrefs(List<String> xrefs) {
            return (Builder) super.xrefs(xrefs);
        }

        @JsonCreator
        public XrefPropertyValue build() {
            return new XrefPropertyValue(this);
        }
    }

}
