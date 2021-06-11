package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.geneontology.obographs.model.axiom.DomainRangeAxiom;
import org.geneontology.obographs.model.axiom.EquivalentNodesSet;
import org.geneontology.obographs.model.axiom.LogicalDefinitionAxiom;
import org.geneontology.obographs.model.axiom.PropertyChainAxiom;

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
@JsonDeserialize(builder = Graph.Builder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Graph {

    private Graph(Builder builder) {
        id = builder.id;
        lbl = builder.lbl;
        meta = builder.meta;
        nodes = builder.nodes;
        edges = builder.edges;
        equivalentNodesSets = builder.equivalentNodesSets;
        logicalDefinitionAxioms = builder.logicalDefinitionAxioms;
        domainRangeAxioms = builder.domainRangeAxioms;
        propertyChainAxioms = builder.propertyChainAxioms;
    }

    private final List<Node> nodes;
    private final List<Edge> edges;
    private final String id;
    private final String lbl;
    private final Meta meta;
    private final List<EquivalentNodesSet> equivalentNodesSets;
    private final List<LogicalDefinitionAxiom> logicalDefinitionAxioms;
    private final List<DomainRangeAxiom> domainRangeAxioms;
    private final List<PropertyChainAxiom> propertyChainAxioms;



    /**
     * @return the nodes
     */
    public List<Node> getNodes() {
        return nodes;
    }



    /**
     * @return the edges
     */
    public List<Edge> getEdges() {
        return edges;
    }



    /**
     * @return the id
     */
    public String getId() {
        return id;
    }



    /**
     * @return the lbl
     */
    public String getLbl() {
        return lbl;
    }



    /**
     * @return the meta
     */
    public Meta getMeta() {
        return meta;
    }



    /**
     * @return the equivalentNodesSet
     */
    public List<EquivalentNodesSet> getEquivalentNodesSets() {
        return equivalentNodesSets;
    }



    /**
     * @return the logicalDefinitionAxioms
     */
    public List<LogicalDefinitionAxiom> getLogicalDefinitionAxioms() {
        return logicalDefinitionAxioms;
    }



    /**
     * @return the domainRangeAxioms
     */
    public List<DomainRangeAxiom> getDomainRangeAxioms() {
        return domainRangeAxioms;
    }



    /**
     * @return the propertyChainAxioms
     */
    public List<PropertyChainAxiom> getPropertyChainAxioms() {
        return propertyChainAxioms;
    }


    @Override
    public String toString() {
        return "Graph{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                ", id='" + id + '\'' +
                ", lbl='" + lbl + '\'' +
                ", meta=" + meta +
                ", equivalentNodesSets=" + equivalentNodesSets +
                ", logicalDefinitionAxioms=" + logicalDefinitionAxioms +
                ", domainRangeAxioms=" + domainRangeAxioms +
                ", propertyChainAxioms=" + propertyChainAxioms +
                '}';
    }

    public static class Builder {

        @JsonProperty
        private String id;
        @JsonProperty
        private String lbl;
        @JsonProperty
        private Meta meta;
        @JsonProperty
        private List<Node> nodes;
        @JsonProperty
        private List<Edge> edges;
        @JsonProperty
        private List<EquivalentNodesSet> equivalentNodesSets;
        @JsonProperty
        private List<LogicalDefinitionAxiom> logicalDefinitionAxioms;
        @JsonProperty
        private List<DomainRangeAxiom> domainRangeAxioms;
        @JsonProperty
        private List<PropertyChainAxiom> propertyChainAxioms;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder lbl(String lbl) {
            this.lbl = lbl;
            return this;
        }

        public Builder meta(Meta meta) {
            this.meta = meta;
            return this;
        }

        // TODO: test for uniqueness
        public Builder nodes(List<Node> nodes) {
            this.nodes = nodes;
            return this;
        }
        public Builder edges(List<Edge> edges) {
            this.edges = edges;
            return this;
        }
        public Builder propertyChainAxioms(List<PropertyChainAxiom> propertyChainAxioms) {
            this.propertyChainAxioms = propertyChainAxioms;
            return this;
        }
        public Builder equivalentNodesSet(List<EquivalentNodesSet> equivalentNodesSets) {
            this.equivalentNodesSets = equivalentNodesSets;
            return this;
        }
        public Builder logicalDefinitionAxioms(List<LogicalDefinitionAxiom> logicalDefinitionAxioms) {
            this.logicalDefinitionAxioms = logicalDefinitionAxioms;
            return this;
        }
        public Builder domainRangeAxioms(List<DomainRangeAxiom> domainRangeAxioms) {
            this.domainRangeAxioms = domainRangeAxioms;
            return this;
        }

        @JsonCreator
        public Graph build() {
            return new Graph(this);
        }
    }

}
