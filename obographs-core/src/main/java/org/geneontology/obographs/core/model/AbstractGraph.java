package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geneontology.obographs.core.model.axiom.DomainRangeAxiom;
import org.geneontology.obographs.core.model.axiom.EquivalentNodesSet;
import org.geneontology.obographs.core.model.axiom.LogicalDefinitionAxiom;
import org.geneontology.obographs.core.model.axiom.PropertyChainAxiom;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

/**
 * A graph object holds a collection of nodes and edges
 * 
 * Corresponds to a Named Graph in RDF, and an Ontology in OWL
 * 
 * Note: there is no assumption that either nodes or edges are unique to a graph
 * 
 * ## Basic OBO Graphs
 * 
 * ![Node UML](node-bog.png)
 *  * 
 * @startuml node-bog.png
 * class Graph
 * class Node
 * class Edge
 * 
 * Graph-->Node : 0..*
 * Graph-->Edge : 0..*
 * @enduml

 * 
 * @author cjm
 *
 */
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"id", "lbl", "meta", "nodes", "edges", "equivalentNodesSets", "logicalDefinitionAxioms", "domainRangeAxioms", "propertyChainAxioms"})
@Value.Immutable
public abstract class AbstractGraph {

    /**
     * @return the id
     */
    @JsonProperty
    @Value.Default
    public String getId() {
        return "";
    }

    /**
     * @return the lbl
     */
    @JsonProperty
    @Value.Default
    public String getLbl() {
        return "";
    }

    /**
     * @return the meta
     */
    @JsonProperty
    @Nullable
    public abstract Meta getMeta();

    /**
     * @return the nodes
     */
    @JsonProperty
    public abstract List<Node> getNodes();

    /**
     * @return the edges
     */
    @JsonProperty
    public abstract List<Edge> getEdges();

    /**
     * @return the equivalentNodesSet
     */
    @JsonProperty
    public abstract List<EquivalentNodesSet> getEquivalentNodesSets();

    /**
     * @return the logicalDefinitionAxioms
     */
    @JsonProperty
    public abstract List<LogicalDefinitionAxiom> getLogicalDefinitionAxioms();

    /**
     * @return the domainRangeAxioms
     */
    @JsonProperty
    public abstract List<DomainRangeAxiom> getDomainRangeAxioms();

    /**
     * @return the propertyChainAxioms
     */
    @JsonProperty
    public abstract List<PropertyChainAxiom> getPropertyChainAxioms();

}
