package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;

import javax.annotation.Nullable;
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
            Comparator.comparing(AbstractEdge::sub)
                    .thenComparing(AbstractEdge::pred)
                    .thenComparing(AbstractEdge::obj);

    @JsonProperty
    public abstract String sub();

    @JsonProperty
    public abstract String pred();

    @JsonProperty
    public abstract String obj();

    @JsonProperty
    @Nullable
    public abstract Meta meta();

    @Override
    public int compareTo(AbstractEdge other) {
        return COMPARATOR.compare(this, other);
    }
}
