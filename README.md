[![Build Status](https://travis-ci.org/geneontology/obographs.svg?branch=master)](https://travis-ci.org/geneontology/obographs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.geneontology/obographs/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.geneontology/obographs)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/org.geneontology/obographs/badge.svg)](http://www.javadoc.io/doc/org.geneontology/obographs)

# OBO Graphs : Developer-friendly graph-oriented ontology JSON/YAML

This repo contains both a specification for a JSON/YAML format for
ontology exchange, plus a reference java object model and OWL
converter.

The core is a simple graph model allowing the expression of ontology
relationships like `forelimb SubClassOf limb`:

```
  "nodes" : [
    {
      "id" : "UBERON:0002102",
      "lbl" : "forelimb"
    }, {
      "id" : "UBERON:0002101",
      "lbl" : "limb"
    }
  ],
  "edges" : [
    {
      "subj" : "UBERON:0002102",
      "pred" : "is_a",
      "obj" : "UBERON:0002101"
    }
  ]
```

Additional optional fields allow increased expressivity, without
adding complexity to the core.

For more examples, see [examples/](examples)

For the JSON Schema, see the [schema/](schema) folder

If you are familiar with OWL, skip straight to the [OWL mapping specification](README-owlmapping.md) 

## Motivation

Currently if a developer needs to add ontologies into a software
framework or tool, there are two options for formats: obo-format and
OWL (technically obo is an OWL syntax, but for pragmatic purposes we
can separate these two).

This presents a number of problems: obo is simple, but employs its own
syntax, resulting in a proliferation of ad-hoc parsers that are
generally incomplete. It is also less expressive than OWL (but
expressive enough for the majority of bioinformatics tasks). OWL is a
W3 standard, but can be difficult to work with. Typically OWL is
layered on RDF, but RDF level libraries can be too low-level to work
with (additionally: rdflib for Python is very slow). For JVM
languages, the OWLAPI can be used, but this can be abstruse for many
routine tasks, leading to variety of simplifying facades each with
their own assumptions (e.g. BRAIN).

## Overview

OBO Graphs (OGs) are a graph-oriented way of representing ontologies
or portions of ontologies in a developer-friendly JSON (or YAML). A
typical consumer may be a Python developer using ontologies to enhance
an analysis tool, database search/infrastructure etc.

The model can be understood as two levels: A __basic__ level, that is
intended to satisfy 99% of bioinformatics use cases, and is
essentially a cytoscape-like nodes and edges model. On top of this is
an __expressive__ level that allows the representation of more
esoteric OWL axioms. 

## Basic OBO Graphs (BOGs)

The core model is a property-labeled graph, comparable to the data
model underlying graph databases such as Neo4j. The format is the same
as [BBOP-Graphs](https://github.com/berkeleybop/bbop-js/wiki/Graph).

The basic form is:

```
"graphs": [
  {
     "nodes" : [...],
     "edges" : [...],
  },
  ...
]
```

Here is an example of a subgraph of Uberon consisting of two nodes and one
part-of edge:


```
{
  "nodes" : [
    {
      "id" : "UBERON:0002470",
      "lbl" : "autopod region"
    }, {
      "id" : "UBERON:0002102",
      "lbl" : "forelimb"
    }, {
      "id" : "UBERON:0002101",
      "lbl" : "limb"
    }, {
      "id" : "UBERON:0002398",
      "lbl" : "manus"
    }
  ],
  "edges" : [
    {
      "subj" : "UBERON:0002102",
      "pred" : "is_a",
      "obj" : "UBERON:0002101"
    }, {
      "subj" : "UBERON:0002398",
      "pred" : "part_of",
      "obj" : "UBERON:0002102"
    }, {
      "subj" : "UBERON:0002398",
      "pred" : "is_a",
      "obj" : "UBERON:0002470"
    }, {
      "subj" : "UBERON:0002470",
      "pred" : "part_of",
      "obj" : "UBERON:0002101"
    }
   ]
}
```

The short forms in the above (e.g. `UBERON:0002470` and `part_of`) are
mapped to unambiguous PURLs using a JSON-LD context (see below).

Edges can also be decorated with `Meta` objects (corresponding to
reification in RDF/OWL, or edge properties in graph databases).

Formally, the set of edges correspond to OWL SubClassOf axioms of two
forms:

 1. `C SubClassOf D` (aka `is_a` in obo-format)
 2. `C SubClassOf P some D`(aka `relationship` in obo-format)

For a full description, see the JSON Schema below

Nodes collect all OWL annotations about an entity.

Typically nodes will be OWL classes, but they can also be OWL
individuals, or OWL properties (in which case edges can also
correspond to SubPropertyOf axioms)

## Expressive OBO Graphs (ExOGs)

These provide ways of expressing logical axioms not covered in the
subset above.

Currently the spec does not provide a complete translation of all OWL
axioms. This will be driven by comments on the spec.

Currently two axiom patterns are defined:

 * equivalenceSet
 * logicalDefinitionAxiom

Note that these do not necessarily correspond 1:1 to OWL axiom
types. The two above are different forms of equivalent classes axiom,
the former suited to cases where we have multiple ontologies with overlapping

See [README-owlmapping.md](README-owlmapping.md) for mor details

## Comparison with BBOP-Graphs

See [bbop-graph](https://github.com/berkeleybop/bbop-graph)

 * Top-level object in a bbop-graph is a `graph` object; in obographs a `GraphDocument` is a holder for multiple graphs
 * `meta` objects are underspecified in bbop-graphs

## Comparison with SciGraph

See [Neo4jMapping](https://github.com/SciGraph/SciGraph/wiki/Neo4jMapping)

The mapping is similar, particularly with respect to how SubClassOf
axioms map to edges. However, for SciGraph, more advanced axioms such
as EquivalenceAxioms are mapped to graph edges. In obographs, anything
outside the BOG pattern is mapped to a custom object.

Note also that SciGraph returns bbop-graph objects by default from
graph query operations.

## Running the converter

```
mvn install
./bin/ogger  src/test/resources/basic.obo 
```

Note that the conversion [will be rolled into tools like ROBOT](https://github.com/geneontology/obographs/issues/5)
obviating the need for this. We can also make it such that the JSON is available from a standard PURL, e.g.

 * http://purl.obolibrary.org/obo/envo.obo
 * http://purl.obolibrary.org/obo/envo.owl
 * http://purl.obolibrary.org/obo/envo.json NEW

## Including obographs in your code:

### Maven
```xml
<dependency>
    <groupId>org.geneontology</groupId>
    <artifactId>obographs</artifactId>
    <version>${project.version}</version>
</dependency>
```

### Gradle
```groovy
compile 'org.geneontology:obographs:${project.version}'
```

### Installing a development snapshot

When developing against an unreleased snapshot version of the API, you can use Maven to install it in your local m2 repository:

```
mvn -Dgpg.skip install
```

## Javascript

See [bbop-graph](https://github.com/berkeleybop/bbop-graph)
