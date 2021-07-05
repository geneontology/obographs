
/**
 * 
 * ## Basic Obo Graph Model
 * 
 * Corresponds to the subset of OWL consisting of SubClassOf axioms between named classes
 * and either named classes or simple existential restrictions.
 * 
 * ![Node UML](graph-package.png)
 *  
 * @startuml graph-package.png
 * class GraphDocument
 * class Graph
 * class Meta
 * class Node
 * class Edge
 * 
 * GraphDocument-->Graph : 0..*
 * GraphDocument-->Meta : 0..*
 * Graph-->Node : 0..*
 * Graph-->Edge : 0..*
 * @enduml
 *
 * 
 * ## Advanced Axioms
 * 
 * See sub-package
 * 
 * ![Node UML](graph-advanced.png)
 *  
 * @startuml graph-advanced.png
 * class GraphDocument
 * class Graph
 * class Meta
 * class Node
 * class Edge
 * class SpecialAxiomType
 * 
 * GraphDocument-->Graph : 0..*
 * GraphDocument-->Meta : 0..*
 * Graph-->Node : 0..*
 * Graph-->Edge : 0..*
 * Graph-->SpecialAxiomType : 0..*
 * @enduml
 * 
 * Note in the above 'SpecialAxiomType' is not actually a class. It is a placeholder for a variety of other axiom types,
 * see for example {@link org.geneontology.obographs.core.model.axiom.LogicalDefinitionAxiom}

 * 
 * @author cjm
 *
 */

@OboGraph
package org.geneontology.obographs.core.model;