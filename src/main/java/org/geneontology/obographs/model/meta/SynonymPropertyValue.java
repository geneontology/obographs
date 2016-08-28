package org.geneontology.obographs.model.meta;

import java.util.List;

import org.geneontology.obographs.model.meta.DefinitionPropertyValue.Builder;


public class SynonymPropertyValue extends AbstractPropertyValue implements PropertyValue {

    public enum SCOPES {
        EXACT,
        NARROW,
        BROAD,
        RELATED
    };
    public enum PREDS {
        hasExactSynonym,
        hasNarrowSynonym,
        hasBroadSynonym,
        hasRelatedSynonym
    };

    private SynonymPropertyValue(Builder builder) {
        super(builder);
    }

    public boolean isExact() {
        return getPred().equals(PREDS.hasExactSynonym.toString());
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

        public Builder scope(SCOPES scope) {
            PREDS pred = PREDS.hasRelatedSynonym;
            switch (scope) {
            case EXACT: pred = PREDS.hasExactSynonym;
            case RELATED: pred = PREDS.hasRelatedSynonym;
            case BROAD: pred = PREDS.hasBroadSynonym;
            case NARROW: pred = PREDS.hasNarrowSynonym;

            }
            super.pred(pred.toString());
            return this;

        }

        public SynonymPropertyValue build() {
            return new SynonymPropertyValue(this);
        }
    }

}
