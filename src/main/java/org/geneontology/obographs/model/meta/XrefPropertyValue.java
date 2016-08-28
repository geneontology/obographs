package org.geneontology.obographs.model.meta;

import java.util.List;

import org.geneontology.obographs.model.meta.DefinitionPropertyValue.Builder;


public class XrefPropertyValue extends AbstractPropertyValue implements PropertyValue {


    private XrefPropertyValue(Builder builder) {
        super(builder);
    }

  
    public static class Builder extends AbstractPropertyValue.Builder {

        @Override
        public Builder val(String val) {
            return (Builder) super.val(val);
        }

        @Override
        public Builder xrefs(List<String> xrefs) {
            return (Builder) super.xrefs(xrefs);
        }

        public XrefPropertyValue build() {
            return new XrefPropertyValue(this);
        }
    }

}
