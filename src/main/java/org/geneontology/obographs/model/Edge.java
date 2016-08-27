package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An edge connects two nodes via a predicate
 * 
 * @author cjm
 *
 */
public class Edge implements NodeOrEdge {
	
	private Edge(Builder builder) {
		subj = builder.subj;
		pred = builder.pred;
		obj = builder.obj;
		meta = builder.meta;
	}

	private final String subj;
	private final String pred;
	private final String obj;
	private final Meta meta;
	
	
	
    /**
	 * @return the subj
	 */
	public String getSubj() {
		return subj;
	}



	/**
	 * @return the pred
	 */
	public String getPred() {
		return pred;
	}



	/**
	 * @return the obj
	 */
	public String getObj() {
		return obj;
	}



	/**
	 * @return the meta
	 */
	public Meta getMeta() {
		return meta;
	}



	public static class Builder {

        @JsonProperty
        private String subj;
        @JsonProperty
        private String pred;
        @JsonProperty
        private String obj;
        
        @JsonProperty
        private Meta meta;
        
        public Builder subj(String subj) {
            this.subj = subj;
            return this;
        }
        public Builder obj(String obj) {
            this.obj = obj;
            return this;
        }

        public Builder pred(String pred) {
            this.pred = pred;
            return this;
        }

        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
        }

        public Edge build() {
        	return new Edge(this);
        }
    }
    
}
