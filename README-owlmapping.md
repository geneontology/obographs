
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

TBD: are all named objects translated to nodes or just OWLClasses?

```
Class: C
  Annotations: rdfs:label N

==>

nodes:
 - id: C
   lbl: N
   type: "CLASS"
```

TODO

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

## Meta objects

Meta objects can be placed at multiple levels:

 1. GraphDocument
 2. Graph
 3. Node
 4. Edge
 5. Axiom

Note for the final two, the Meta object corresponds to _reification_ in RDF/OWL

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


## Synonyms

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

