package org.geneontology.obographs.model.meta;

import java.util.List;

import org.geneontology.obographs.model.meta.DefinitionPropertyValue.Builder;


public class XrefPropertyValue extends AbstractPropertyValue implements PropertyValue {

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

        public XrefPropertyValue build() {
            return new XrefPropertyValue(this);
        }
    }

}
