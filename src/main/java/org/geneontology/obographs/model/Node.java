package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ComparisonChain;

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
@JsonDeserialize(builder = Node.Builder.class)
public class Node implements NodeOrEdge, Comparable<Node> {
	
    public enum RDFTYPES { CLASS, INDIVIDUAL, PROPERTY };
    
	private Node(Builder builder) {
		id = builder.id;
		label = builder.label;
		meta = builder.meta;
		type = builder.type;
	}

    private final Meta meta;
    private final String id;
    private final String label;
	private final RDFTYPES type;

    /**
     * @return the meta
     */
    public Meta getMeta() {
        return meta;
    }

    /**
	 * @return the id
	 */
    @JsonProperty
    public String getId() {
		return id;
	}

	/**
	 * @return the lbl
	 */
    @JsonProperty("lbl")
    public String getLabel() {
		return label;
	}

	/**
     * @return the type
     */
    @JsonProperty
    public RDFTYPES getType() {
        return type;
    }


    @Override
    public int compareTo(Node other) {
        return ComparisonChain.start()
                .compare(this.getId(), other.getId())
                .compare(this.getLabel(), other.getLabel())
                .compare(this.getType(), other.getType())
                .result();
    }

    @Override
    public String toString() {
        return "Node{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", meta=" + meta +
                ", type=" + type +
                '}';
    }

    public static class Builder {

        @JsonProperty
        private String id;
        @JsonProperty("lbl")
        private String label;
        @JsonProperty
        private Meta meta;
        @JsonProperty
        private RDFTYPES type;
        
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
        }

        public Builder type(RDFTYPES type) {
            this.type = type;
            return this;
        }

        @JsonCreator
        public Node build() {
        	return new Node(this);
        }
    }
    
}
