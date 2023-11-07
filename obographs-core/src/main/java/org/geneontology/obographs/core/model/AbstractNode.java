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
            Comparator.comparing(AbstractNode::getId)
                    .thenComparing(AbstractNode::getLabel)
                    .thenComparing(AbstractNode::getType, nullsLast(naturalOrder()))
                    .thenComparing(AbstractNode::getPropertyType, nullsLast(naturalOrder()));

    public enum RDFTYPES {CLASS, INDIVIDUAL, PROPERTY}

    public enum PropertyType {ANNOTATION, OBJECT, DATA}

    @JsonProperty
    @Value.Default
    public String getId() {
        return "";
    }

    @JsonProperty("lbl")
    @Value.Default
    public String getLabel() {
        return "";
    }

    @JsonProperty
    @Nullable
    public abstract RDFTYPES getType();

    @JsonProperty
    @Nullable
    public abstract PropertyType getPropertyType();

    @Override
    public int compareTo(AbstractNode other) {
        return COMPARATOR.compare(this, other);
    }

}
