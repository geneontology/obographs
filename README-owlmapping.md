
This document defines the mapping between OWL and OBOGraphs. We use
the more compact YAML form to specify the mapping.

For the reference implemetation, see the [FromOwl](https://github.com/geneontology/obographs/blob/master/src/main/java/org/geneontology/obographs/owlapi/FromOwl.java) class.

## URIs and Context objects

Every identifier in an OBO Graph is iterpreted as a URI. At the
JSON/YAML level, these may be compacted URIs. The expansion to a full
URI is specified via a JSON-LD context object. Context objects may be
at the level of the graph document or an individual graph.

## Nodes

All nodes go in a nodes array object, placed immediately under the graph object


```
Class: C
  Annotations: rdfs:label N

==>

nodes:
 - id: C
   lbl: N
   type: "CLASS"
```

Analogous rules apply to individuals and properties

## Edges

Two edge types:

```
Class: C
  SubClassOf: D

==>

edges:
 - subj: C
   pred: is_a
   obj: D
```


```
Class: C
  SubClassOf: P some D

==>

edges:
 - subj: C
   pred: P
   obj: D
```

Note that universal restrictions are not added into the main graph

Value restrictions are treated in an analogous fashion to existentials (this pattern should be rare for most bio-ontologies):

```
Class: C
  SubClassOf: P value J

==>

edges:
 - subj: C
   pred: P
   obj: J
```

STATUS: UNIMPLEMETED

See https://github.com/geneontology/obographs/issues/7#issuecomment-260735549


## Handling of universal (all values from) axioms

These do not pollute the main `edges` list

```
Class: C
  SubCLassOf: P only D

allValuesFromEdges:
 - sub: C
   pred: P
   obj: D
```


## LogicalDefinitionAxioms

All logical definitions go in a logicalDefinitionAxioms array object, placed immediately under the meta object

Given:

```
Class: C
EquivalentTo: G1 and ... and Gn and (P1 some D1) and ... and (Pm some Dm)
```

Where all variables refer to named entities (C, Gi and Di are classes,
Pi are Object Properties)

We translate to:

```
  - definedClassId: C
    genusIds: [G1, ..., Gn]
    restrictions:
    - propertyId: P1 
      fillerId: D1
    - ...
    - propertyId: Pm 
      fillerId: Dm
```

## RBox Axioms

### SubPropertyOf Axioms

These treated as graph edges

```
ObjectProperty: P
  SubPropertyOf: Q

==>

edges:
 - subj: P
   pred: subPropertyOf
   obj: Q
```

### InversePropertyOf Axioms

These treated as graph edges

```
ObjectProperty: P
  InverseOf: Q

==>

edges:
 - subj: P
   pred: inverseOf
   obj: Q
```

### Property Chain Axioms


```
ObjectProperty: P
  SubPropertyChain: P1 o P2  

==>

propertyChainAxioms:
 - predicateId: P
   chainPredicateIds: [P1, P2]
```

### Domain and Range Axioms


```
ObjectProperty: P
  Domain: D
  Range: R

==>

domainRangeAxioms:
 - predicateId: P
   domainClassIds: [D]
   rangeClassIds: [R]
```

## ABox Axioms

For examples, see [abox.owl](src/test/resources/abox.owl) and its translation [abox.json](examples/abox.json)

### ObjectPropertyAssertion Axioms (Facts)

These treated as graph edges

```
Individual: I
  Facts: P J

==>

edges:
 - subj: I
   pred: P
   obj: J
```

TODO: Negative ObjectPropertyAssertions

### ClassAssertion Axioms (type axioms)

When the class expression is a named class, these are also treated as graph edges

```
Individual: I
  Types: C

==>

edges:
 - subj: I
   pred: type
   obj: C
```

When the class expression is a simple existential restriction, this is also treated as a graph edge

```
Individual: I
  Types: P some D

==>

edges:
 - subj: I
   pred: P
   obj: D
```

STATUS: UNIMPLEMETED

See https://github.com/geneontology/obographs/issues/7#issuecomment-260735549




## Meta objects

Meta objects can be placed at multiple levels:

 1. GraphDocument
 2. Graph
 3. Node
 4. Edge
 5. Axiom

Each element Meta object corresponds to an Annotation in OWL

Note for the final two, the Meta object corresponds to _reification_ in RDF/OWL

The meta object looks like:

```
meta:
  definition: DEFINITION
  subsets: SUBSET-LIST
  xrefs: XREF-LIST
  synonyms: SYN-LIST
  comments: COMMENT-LIST
  basicPropertyValues: BPV-LIST
```

See the OBO-Syntax spec for details

If the meta object is at the level of a Graph (Ontology) then it may optionally have a version field, correspondig to the owl versionIRI

### Synonym Meta objects

Example:

```
      synonyms:
      - pred: "hasExactSynonym"
        val: "intracellular membrane-enclosed organelle"
        xrefs: []
```

All synonyms go in a `synonyms` array object, placed immediately under the meta object

We specify the mapping in terms of OBO syntax as this is more compact

Given:

```
id: C
synonym: "VAL" SCOPE [XREF1, ..., XREFn]
```


We translate to:

```
      synonyms:
      - pred: "PRED"
        val: "VAL"
        xrefs: [XREF1, ..., XREFn]
```

where the mapping between SCOPE (e.g. EXACT, NARROW, ...) and PRED is defined by [the OBO to OWL annotation vocabulary translation](http://owlcollab.github.io/oboformat/doc/obo-syntax.html#5.8)

