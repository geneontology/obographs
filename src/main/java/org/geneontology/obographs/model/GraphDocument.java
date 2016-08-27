package org.geneontology.obographs.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds a collection of graphs, plus document-level metadata
 * 
 * @author cjm
 *
 */
public class GraphDocument {

    private GraphDocument(Builder builder) {
        meta = builder.meta;
        graphs = builder.graphs;
        context = builder.context;
   }

    private final List<Graph> graphs;
    private final Meta meta;

    /**
     * The JSON-LD context for this document. This needs to be an unstructured
     * Object, since it could be either a list or a map. We don't want to store
     * it here as a Context because we want to roundtrip it the way it is written
     * in the document.
     */
    @JsonProperty("@context")
    private final Object context;


    /**
     * @return the graphs
     */
    public List<Graph> getGraphs() {
        return graphs;
    }



    /**
     * @return the meta
     */
    public Meta getMeta() {
        return meta;
    }






    public static class Builder {

        @JsonProperty
        private Meta meta;
        @JsonProperty
        private List<Graph> graphs;
        @JsonProperty
        private Object context;



        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
        }
        public Builder graphs(List<Graph> graphs) {
            this.graphs = graphs;
            return this;
        }
        public Builder context(Object context) {
            this.context = context;
            return this;
        }

        public GraphDocument build() {
            return new GraphDocument(this);
        }

    }

}
