package org.geneontology.obographs.core.model.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;

import java.util.Collections;
import java.util.List;


/**
 * A {@link PropertyValue} that represents a an alternative term for a node
 *
 * @author cjm
 */
@JsonPropertyOrder({"synonymType", "pred", "val", "xrefs", "meta"})
@Value.Immutable
public abstract class AbstractSynonymPropertyValue implements PropertyValue {

    /**
     * OBO-style synonym scopes
     *
     * @author cjm
     */
    public enum Scope {
        EXACT,
        NARROW,
        BROAD,
        RELATED;

        public String pred() {
            return switch (this) {
                case EXACT -> "hasExactSynonym";
                case NARROW -> "hasNarrowSynonym";
                case BROAD -> "hasBroadSynonym";
                case RELATED -> "hasRelatedSynonym";
            };
        }
    }

    @JsonProperty
    @Value.Default
    public String synonymType() {
        return "";
    }

    /**
     * @return true if scope equals EXACT -- convenience predicate
     */
    @JsonIgnore
    public boolean isExact() {
        return Scope.EXACT.pred().equals(pred());
    }

    @JsonIgnore
    public boolean isRelated() {
        return Scope.RELATED.pred().equals(pred());
    }

    @JsonIgnore
    public boolean isBroad() {
        return Scope.BROAD.pred().equals(pred());
    }

    @JsonIgnore
    public boolean isNarrow() {
        return Scope.NARROW.pred().equals(pred());
    }

    @JsonIgnore
    public List<String> types() {
        if (meta() != null) {
            return meta().subsets();
        }
        return Collections.emptyList();
    }
}
