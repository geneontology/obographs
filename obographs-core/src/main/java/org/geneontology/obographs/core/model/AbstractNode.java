package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Comparator;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

/**
 * A graph node corresponds to a class, individual or property
 * <p>
 * ![Node UML](node-uml.png)
 * 
 * @startuml node-uml.png
 * class Node {
 *   String id
 * }
 * class Meta
 * 
 * Node-->Meta : 0..1
 * @enduml
 * 
 * @author cjm
 *
 */
@JsonPropertyOrder({"id", "lbl", "type", "propertyType", "meta"})
@Value.Immutable
public abstract class AbstractNode implements NodeOrEdge, Comparable<AbstractNode> {

    private static final Comparator<AbstractNode> COMPARATOR =
            Comparator.comparing(AbstractNode::id)
                    .thenComparing(AbstractNode::label)
                    .thenComparing(AbstractNode::rdfType, nullsLast(naturalOrder()))
                    .thenComparing(AbstractNode::propertyType, nullsLast(naturalOrder()));

    @JsonProperty
    @Value.Default
    public String id() {
        return "";
    }

    @JsonProperty("lbl")
    @Value.Default
    public String label() {
        return "";
    }

    @JsonProperty("type")
    @Nullable
    public abstract RdfType rdfType();

    @JsonProperty
    @Nullable
    public abstract PropertyType propertyType();

    @Override
    public int compareTo(AbstractNode other) {
        return COMPARATOR.compare(this, other);
    }

}
