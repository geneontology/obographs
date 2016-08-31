package org.geneontology.obographs.model.meta;

import java.util.List;

import org.geneontology.obographs.model.meta.DefinitionPropertyValue.Builder;

import com.fasterxml.jackson.annotation.JsonIgnore;


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

    @JsonIgnore
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
            case EXACT: pred = PREDS.hasExactSynonym; break;
            case RELATED: pred = PREDS.hasRelatedSynonym; break;
            case BROAD: pred = PREDS.hasBroadSynonym; break;
            case NARROW: pred = PREDS.hasNarrowSynonym; break;

            }
            super.pred(pred.toString());
            return this;

        }

        public SynonymPropertyValue build() {
            return new SynonymPropertyValue(this);
        }
    }

}
