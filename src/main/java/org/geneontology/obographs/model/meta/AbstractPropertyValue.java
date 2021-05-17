package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geneontology.obographs.model.Meta;

import java.util.List;

public abstract class AbstractPropertyValue implements PropertyValue {
	


    protected AbstractPropertyValue(Builder builder) {
		pred = builder.pred;
		val = builder.val;
		meta = builder.meta;
		xrefs = builder.xrefs;
	}

    private final String pred;
    private final String val;
    private final List<String> xrefs;
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
     * @return the xrefs
     */
    public List<String> getXrefs() {
        return xrefs;
    }

	/**
	 * @return the meta
	 */
	public Meta getMeta() {
		return meta;
	}

    @Override
    public String toString() {
        return "AbstractPropertyValue{" +
                "pred='" + pred + '\'' +
                ", val='" + val + '\'' +
                ", xrefs=" + xrefs +
                ", meta=" + meta +
                '}';
    }

    public static class Builder {

        @JsonProperty
        private String pred;
        @JsonProperty
        private String val;
        @JsonProperty
        private Meta meta;
        @JsonProperty
        private List<String> xrefs;
        
        public Builder pred(String pred) {
            this.pred = pred;
            return this;
        }

        public Builder val(String val) {
            this.val = val;
            return this;
        }
        public Builder xrefs(List<String> xrefs) {
            this.xrefs = xrefs;
            return this;
        }

        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
        }

 

    }
    
}
