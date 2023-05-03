package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ComparisonChain;
import org.immutables.value.Value;

import javax.annotation.Nullable;

/**
 * A graph node corresponds to a class, individual or property
 * 
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
	
    public enum RDFTYPES { CLASS, INDIVIDUAL, PROPERTY }

	public enum PropertyType { ANNOTATION, OBJECT, DATA }

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

	public int compareTo(AbstractNode other) {
		return ComparisonChain.start()
				.compare(this.getId(), other.getId())
				.compare(this.getLabel(), other.getLabel())
				.compare(this.getType(), other.getType())
				.compare(this.getPropertyType(), other.getPropertyType())
				.result();
	}
}
