package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@JsonSerialize(as = Node.class)
@JsonDeserialize(as = Node.class)
@Value.Immutable
public abstract class AbstractNode implements NodeOrEdge {
	
    public enum RDFTYPES { CLASS, INDIVIDUAL, PROPERTY };

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

}
