package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A graph node corresponds to a class, individual or property
 * 
 * @author cjm
 *
 */
public class Node implements NodeOrEdge {
	
	private Node(Builder builder) {
		id = builder.id;
		label = builder.label;
		meta = builder.meta;
	}

	private final String id;
	
	@JsonProperty("lbl")
	private final String label;
	private final Meta meta;
	
	
	
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

        public Node build() {
        	return new Node(this);
        }
    }
    
}
