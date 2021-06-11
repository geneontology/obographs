package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * Holds a collection of graphs, plus document-level metadata
 * 
 * ## Model
 * 
 * ![Node UML](graphdocument.png)
 *  
 * @startuml graphdocument.png
 * class GraphDocument
 * class Graph
 * class Meta
 * 
 * GraphDocument-->Graph : 0..*
 * GraphDocument-->Meta : 0..*
 * @enduml
 * 
 * @author cjm
 *
 */
@JsonDeserialize(builder = GraphDocument.Builder.class)
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


    @Override
    public String toString() {
        return "GraphDocument{" +
                "graphs=" + graphs +
                ", meta=" + meta +
                ", context=" + context +
                '}';
    }

    public static class Builder {

        @JsonProperty
        private Meta meta;
        @JsonProperty
        private List<Graph> graphs;
        @JsonProperty("@context")
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

        @JsonCreator
        public GraphDocument build() {
            return new GraphDocument(this);
        }

    }

}
