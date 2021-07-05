package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
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
@JsonPropertyOrder({"context", "meta", "graphs"})
@Value.Immutable
public abstract class AbstractGraphDocument {

    /**
     * The JSON-LD context for this document. This needs to be an unstructured
     * Object, since it could be either a list or a map. We don't want to store
     * it here as a Context because we want to roundtrip it the way it is written
     * in the document.
     */
    @JsonProperty("@context")
    @Nullable
    public abstract Object getContext();

    /**
     * @return the meta
     */
    @JsonProperty
    @Nullable
    public abstract Meta getMeta() ;

    /**
     * @return the graphs
     */
    @JsonProperty
    public abstract List<Graph> getGraphs();

}
