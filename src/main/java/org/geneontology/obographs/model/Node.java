package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class Node implements NodeOrEdge {
	
    public enum RDFTYPES { CLASS, INDIVIDUAL, PROPERTY };
    
	private Node(Builder builder) {
		id = builder.id;
		label = builder.label;
		meta = builder.meta;
		type = builder.type;
	}

	private final String id;
	
	@JsonProperty("lbl")
	private final String label;

	@JsonProperty
	private final Meta meta;
    
	@JsonProperty
	private final RDFTYPES type;
	
	
	
    /**
	 * @return the id
	 */
	public String getId() {
		return id;
	}



	/**
	 * @return the lbl
	 */
	public String getLabel() {
		return label;
	}



	/**
     * @return the type
     */
    public RDFTYPES getType() {
        return type;
    }



    /**
	 * @return the meta
	 */
	public Meta getMeta() {
		return meta;
	}

	public static class Builder {

        @JsonProperty
        private String id;
        @JsonProperty
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

        public Node build() {
        	return new Node(this);
        }
    }
    
}
