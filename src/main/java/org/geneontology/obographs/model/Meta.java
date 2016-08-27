package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Meta {
	
	private Meta(Builder builder) {
		pred = builder.pred;
		val = builder.val;
		meta = builder.meta;
	}

	private final String pred;
	private final String val;
	private final Meta meta;
	
	




	/**
	 * @return the pred
	 */
	public String getPred() {
		return pred;
	}



	/**
	 * @return the val
	 */
	public String getVal() {
		return val;
	}



	/**
	 * @return the meta
	 */
	public Meta getMeta() {
		return meta;
	}



	public static class Builder {

        @JsonProperty
        private String pred;
        @JsonProperty
        private String val;
        @JsonProperty
        private Meta meta;
        
        public Builder pred(String pred) {
            this.pred = pred;
            return this;
        }

        public Builder val(String val) {
            this.val = val;
            return this;
        }

        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
        }

        public Meta build() {
        	return new Meta(this);
        }
    }
    
}
