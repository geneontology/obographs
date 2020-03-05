package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ComparisonChain;

/**
 * An edge connects two nodes via a predicate
 * 
 * @author cjm
 *
 */
@JsonDeserialize(builder = Edge.Builder.class)
public class Edge implements NodeOrEdge, Comparable<Edge> {
	
	private Edge(Builder builder) {
		sub = builder.sub;
		pred = builder.pred;
		obj = builder.obj;
		meta = builder.meta;
	}

	private final String sub;
	private final String pred;
	private final String obj;
    private final Meta meta;	
	
	
    /**
	 * @return the subj
	 */
	public String getSub() {
		return sub;
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

	@Override
	public String toString() {
		return "Edge{" +
				"sub='" + sub + '\'' +
				", pred='" + pred + '\'' +
				", obj='" + obj + '\'' +
				", meta=" + meta +
				'}';
	}

	/**
	 * @return the meta
	 */
	public Meta getMeta() {
		return meta;
	}

	@Override
	public int compareTo(Edge other) {
		return ComparisonChain.start()
				.compare(this.getSub(), other.getSub())
				.compare(this.getPred(), other.getPred())
				.compare(this.getObj(), other.getObj())
				.result();
	}

	public static class Builder {

		@JsonProperty
		private String sub;
		@JsonProperty
		private String pred;
		@JsonProperty
		private String obj;

        private Meta meta;

        public Builder sub(String subj) {
            this.sub = subj;
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

        @JsonCreator
        public Edge build() {
        	return new Edge(this);
        }
    }
    
}
