package org.geneontology.obographs.model.meta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.meta.DefinitionPropertyValue.Builder;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * A {@link PropertyValue} that represents a an alternative term for a node
 * 
 * @author cjm
 *
 */

public class SynonymPropertyValue extends AbstractPropertyValue implements PropertyValue {

    /**
     * OBO-style synonym scopes
     * 
     * @author cjm
     *
     */
    public enum SCOPES {
        EXACT,
        NARROW,
        BROAD,
        RELATED
    };
    
    /**
     * properties from oboInOwl vocabulary that represent scopes
     * 
     * @author cjm
     *
     */
    public enum PREDS {
        hasExactSynonym,
        hasNarrowSynonym,
        hasBroadSynonym,
        hasRelatedSynonym
    };

    private SynonymPropertyValue(Builder builder) {
        super(builder);
    }

    /**
     * @return true is scope equals EXACT -- convenience predicate
     */
    @JsonIgnore
    public boolean isExact() {
        return getPred().equals(PREDS.hasExactSynonym.toString());
    }

    @JsonIgnore
    public List<String> getTypes() {
        if (getMeta() != null) {
            return getMeta().getSubsets();
        }
        return new ArrayList<>();
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

        public Builder addType(String type) {
            // TODO: decide on pattern for nested builders
            super.meta(new Meta.Builder().subsets(Collections.singletonList(type)).build());
            return this;
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
