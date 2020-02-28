package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.Collections;
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

    @JsonProperty
    public abstract String getSynonymType();

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
        return Collections.emptyList();
    }

    // n.b. this was never used in any production code. Left it here for audit purposed, but this should be handled by
    // the FromOwl class adding to the SynonymPropertyValue.Meta.
//        public Builder addType(String type) {
//            // TODO: decide on pattern for nested builders
//            super.meta(new Meta.Builder().subsets(Collections.singletonList(type)).build());
//            return this;
//        }

}
