package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;


/**
 * A {@link PropertyValue} that represents a an alternative term for a node
 *
 * @author cjm
 */
@JsonSerialize(as = SynonymPropertyValue.class)
@JsonDeserialize(as = SynonymPropertyValue.class)
@Value.Immutable
public abstract class AbstractSynonymPropertyValue implements PropertyValue {

    /**
     * OBO-style synonym scopes
     *
     * @author cjm
     */
    public enum SCOPES {
        EXACT,
        NARROW,
        BROAD,
        RELATED;

        public String pred() {
            switch (this) {
                case EXACT:
                    return "hasExactSynonym";
                case RELATED:
                    return "hasRelatedSynonym";
                case BROAD:
                    return "hasBroadSynonym";
                case NARROW:
                    return "hasNarrowSynonym";
                default:
                    return "hasRelatedSynonym";
            }
        }
    }

//    /**
//     * properties from oboInOwl vocabulary that represent scopes
//     *
//     * @author cjm
//     */
//    public enum PREDS {
//        hasExactSynonym,
//        hasNarrowSynonym,
//        hasBroadSynonym,
//        hasRelatedSynonym
//    }

    public abstract String getSynonymType();

//    public void scope(SCOPES scope) {
//        PREDS pred = PREDS.hasRelatedSynonym;
//        switch (scope) {
//            case EXACT: pred = PREDS.hasExactSynonym; break;
//            case RELATED: pred = PREDS.hasRelatedSynonym; break;
//            case BROAD: pred = PREDS.hasBroadSynonym; break;
//            case NARROW: pred = PREDS.hasNarrowSynonym; break;
//        }
//        super.pred(pred.toString());
//    }

    /**
     * @return true is scope equals EXACT -- convenience predicate
     */
    @JsonIgnore
    public boolean isExact() {
        return getPred().equals("hasExactSynonym");
    }

    @JsonIgnore
    public List<String> getTypes() {
        if (getMeta() != null) {
            return getMeta().getSubsets();
        }
        return new ArrayList<>();
    }

//    public static class Builder extends AbstractPropertyValue.Builder {
//
//        @JsonProperty
//        private String synonymType;
//
//        @Override
//        public Builder val(String val) {
//            return (Builder) super.val(val);
//        }
//
//        @Override
//        public Builder xrefs(List<String> xrefs) {
//            return (Builder) super.xrefs(xrefs);
//        }
//
//        public Builder addType(String type) {
//            // TODO: decide on pattern for nested builders
//            super.meta(new Meta.Builder().subsets(Collections.singletonList(type)).build());
//            return this;
//        }
//
//        public Builder scope(SCOPES scope) {
//            PREDS pred = PREDS.hasRelatedSynonym;
//            switch (scope) {
//                case EXACT: pred = PREDS.hasExactSynonym; break;
//                case RELATED: pred = PREDS.hasRelatedSynonym; break;
//                case BROAD: pred = PREDS.hasBroadSynonym; break;
//                case NARROW: pred = PREDS.hasNarrowSynonym; break;
//            }
//            super.pred(pred.toString());
//            return this;
//        }
//
//        public Builder synonymType(String synonymType) {
//            if (synonymType != null)
//                this.synonymType = synonymType;
//            return this;
//        }
//
//        @JsonCreator
//        public SynonymPropertyValue build() {
//            return new SynonymPropertyValue(this);
//        }
//    }

}