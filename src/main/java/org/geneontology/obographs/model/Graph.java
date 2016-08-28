package org.geneontology.obographs.model;

import java.util.List;

import org.geneontology.obographs.model.axiom.EquivalentNodesSet;
import org.geneontology.obographs.model.axiom.LogicalDefinitionAxiom;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A graph object holds a collection of nodes and edges
 * 
 * Corresponds to a Named Graph in RDF, and an Ontology in OWL
 * 
 * Note: there is no assumption that either nodes or edges are unique to a graph
 * 
 * @author cjm
 *
 */
public class Graph {
	
	private Graph(Builder builder) {
		id = builder.id;
		lbl = builder.lbl;
		meta = builder.meta;
		nodes = builder.nodes;
		edges = builder.edges;
        equivalentNodesSets = builder.equivalentNodesSets;
        logicalDefinitionAxioms = builder.logicalDefinitionAxioms;
	}

	private final List<Node> nodes;
	private final List<Edge> edges;
	private final String id;
	private final String lbl;
    private final Meta meta;
    private final List<EquivalentNodesSet> equivalentNodesSets;
    private final List<LogicalDefinitionAxiom> logicalDefinitionAxioms;
	
	
	
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
        public Builder equivalentNodesSet(List<EquivalentNodesSet> equivalentNodesSets) {
            this.equivalentNodesSets = equivalentNodesSets;
            return this;
        }
        public Builder logicalDefinitionAxioms(List<LogicalDefinitionAxiom> logicalDefinitionAxioms) {
            this.logicalDefinitionAxioms = logicalDefinitionAxioms;
            return this;
        }

        public Graph build() {
        	return new Graph(this);
        }
    }
    
}
