package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;

import java.util.Comparator;

/**
 * An edge connects two nodes via a predicate
 *
 * @author cjm
 */
@JsonPropertyOrder({"sub", "pred", "obj", "meta"})
@Value.Immutable
public abstract class AbstractEdge implements NodeOrEdge, Comparable<AbstractEdge> {

    private static final Comparator<AbstractEdge> COMPARATOR =
            Comparator.comparing(AbstractEdge::getSub)
                    .thenComparing(AbstractEdge::getPred)
                    .thenComparing(AbstractEdge::getObj);

    @JsonProperty
    public abstract String getSub();

    @JsonProperty
    public abstract String getPred();

    @JsonProperty
    public abstract String getObj();

    public int compareTo(AbstractEdge other) {
        return COMPARATOR.compare(this, other);
    }
}
