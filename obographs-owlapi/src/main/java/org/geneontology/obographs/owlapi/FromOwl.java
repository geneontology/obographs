package org.geneontology.obographs.owlapi;

import com.github.jsonldjava.core.Context;
import org.geneontology.obographs.core.model.*;
import org.geneontology.obographs.core.model.axiom.*;
import org.geneontology.obographs.core.model.meta.AbstractSynonymPropertyValue.Scope;
import org.geneontology.obographs.core.model.meta.BasicPropertyValue;
import org.geneontology.obographs.core.model.meta.DefinitionPropertyValue;
import org.geneontology.obographs.core.model.meta.SynonymPropertyValue;
import org.geneontology.obographs.core.model.meta.XrefPropertyValue;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements OWL to OG translation
 * ===
 * <p>
 * See <a href="https://github.com/geneontology/obographs/blob/master/README-owlmapping.md">OWL Mapping spec</a>
 *
 * @author cjm
 * @see "[SPEC](https://github.com/geneontology/obographs/blob/master/README-owlmapping.md)"
 */
public class FromOwl {

    private static final Logger LOGGER = LoggerFactory.getLogger(FromOwl.class);

    public static final String SUBCLASS_OF = "is_a";
    public static final String SUBPROPERTY_OF = "subPropertyOf";
    public static final String INVERSE_OF = "inverseOf";
    public static final String TYPE = "type";

    private final PrefixHelper prefixHelper;
    private final Context context;

    static class OBOClassDef {
        final List<String> genusClassIds = new ArrayList<>();
        final List<ExistentialRestrictionExpression> restrs = new ArrayList<>();
    }

    /**
     *
     */
    public FromOwl() {
        prefixHelper = new PrefixHelper();
        context = prefixHelper.getContext();
    }

    /**
     * @param baseOntology
     * @return GraphDocument where each graph is an ontology in the ontology closure
     * @see <a href="https://github.com/geneontology/obographs/blob/master/README-owlmapping.md">OWL Mapping spec</a>
     */
    public GraphDocument generateGraphDocument(OWLOntology baseOntology) {
        List<Graph> graphs = new ArrayList<>();
        for (OWLOntology ont : baseOntology.getImportsClosure()) {
            graphs.add(generateGraph(ont));
        }
        return new GraphDocument.Builder().graphs(graphs).build();
    }

    /**
     * @param ontology
     * @return Graph generated from ontology
     */
    public Graph generateGraph(OWLOntology ontology) {

        OWLOntologyID ontId = ontology.getOntologyID();
        String graphId = ontId.getOntologyIRI().isPresent() ? getNodeId(ontId.getOntologyIRI().get()) : "";
        String version = ontId.getVersionIRI().isPresent() ? getNodeId(ontId.getVersionIRI().get()) : "";
        Meta graphAnnotationsMeta = buildMeta(ontology.getAnnotations(), version);
        // The oboGraphBuilder is where all the OboGraphs stuff is put as it is translated from the OWLOntology
        OboGraphBuilder oboGraphBuilder = new OboGraphBuilder(graphId, graphAnnotationsMeta);

        // Ensure axioms are sorted for reproducible objects and output
        List<OWLAxiom> sortedAxioms = new ArrayList<>(ontology.getAxioms());
        Collections.sort(sortedAxioms);

        // iterate over all axioms and push to relevant builders
        for (OWLAxiom ax : sortedAxioms) {
            Meta meta = buildMeta(ax);
            switch (ax) {
                case OWLDeclarationAxiom dax -> convertOWLDeclarationAxiom(dax, oboGraphBuilder);
                case OWLLogicalAxiom owlLogicalAxiom -> convertOWLLogicalAxiom(owlLogicalAxiom, oboGraphBuilder, meta);
                case OWLAnnotationAssertionAxiom aaa -> convertOWLAnnotationAssertionAxiom(aaa, oboGraphBuilder, meta);
                default -> oboGraphBuilder.addUntranslatedAxiom(ax);
            }
        }

        List<OWLAxiom> untranslatedAxioms = oboGraphBuilder.untranslatedAxioms();
        if (!untranslatedAxioms.isEmpty()) {
            LOGGER.warn("{} contains {} untranslated axioms:", graphId, untranslatedAxioms.size());
            untranslatedAxioms.forEach(axiom -> LOGGER.warn("{}", axiom));
        }

        // build the OboGraph
        return oboGraphBuilder.buildGraph();
    }

    private void convertOWLDeclarationAxiom(OWLDeclarationAxiom dax, OboGraphBuilder oboGraphBuilder) {
        OWLEntity e = dax.getEntity();
        String id = e.getIRI().toString();
        switch (e) {
            case OWLClass ignored -> oboGraphBuilder.addNodeType(id, RdfType.CLASS);
            case OWLNamedIndividual ignored -> oboGraphBuilder.addNodeType(id, RdfType.INDIVIDUAL);
            case OWLDataProperty ignored -> oboGraphBuilder.addNodeType(id, PropertyType.DATA);
            case OWLObjectProperty ignored -> oboGraphBuilder.addNodeType(id, PropertyType.OBJECT);
            case OWLAnnotationProperty ignored -> oboGraphBuilder.addNodeType(id, PropertyType.ANNOTATION);
            default -> {
                // ignore
            }
        }
    }

    // LOGICAL AXIOMS
    private void convertOWLLogicalAxiom(OWLLogicalAxiom owlLogicalAxiom, OboGraphBuilder oboGraphBuilder, Meta meta) {
        switch (owlLogicalAxiom) {
            case OWLSubClassOfAxiom sca -> convertOWLSubClassOfAxiom(sca, oboGraphBuilder, meta);
            case OWLClassAssertionAxiom ca -> convertOWLClassAssertionAxiom(ca, oboGraphBuilder, meta);
            case OWLObjectPropertyAssertionAxiom opa -> convertOWLObjectPropertyAssertionAxiom(opa, oboGraphBuilder, meta);
            case OWLEquivalentClassesAxiom eca -> convertOWLEquivalentClassesAxiom(eca, meta, oboGraphBuilder);
            case OWLObjectPropertyAxiom objectPropertyAxiom -> convertOWLObjectPropertyAxiom(objectPropertyAxiom, oboGraphBuilder, meta);
            default -> oboGraphBuilder.addUntranslatedAxiom(owlLogicalAxiom);
        }
    }

    private void convertOWLSubClassOfAxiom(OWLSubClassOfAxiom sca, OboGraphBuilder oboGraphBuilder, Meta meta) {
        OWLClassExpression subc = sca.getSubClass();

        if (subc.isAnonymous()) {
            // GCI
            /*
             * TBD: Model for GCIs. See https://github.com/obophenotype/uberon/wiki/Evolutionary-variability-GCIs
            if (subc instanceof OWLObjectIntersectionOf) {
                OBOClassDef cdef = getClassDef(((OWLObjectIntersectionOf)subc).getOperands());
                if (cdef.genusClassIds.size() == 1) {
                    subj = cdef.genusClassIds.get(0);
                }

                if (subc instanceof OWLObjectSomeValuesFrom) {
                    // Some axioms such as phylogenetic GCIs are encoded this way
                    ExistentialRestrictionExpression r = getRestriction(subc);
                    subj = r.getFillerId();
                }
            }
             */
        } else if (subc.isNamed()) {
            // SUBJECT IS NAMED CLASS (non-GCI)
            convertNamedSubClassOfAxiom(sca, oboGraphBuilder, meta);
        } else {
            // Logically impossible to reach this? subj is either named or anonymous
            oboGraphBuilder.addUntranslatedAxiom(sca);
        }
    }

    private void convertNamedSubClassOfAxiom(OWLSubClassOfAxiom sca, OboGraphBuilder oboGraphBuilder, Meta meta) {
        String subj = getClassId((OWLClass) sca.getSubClass());
        oboGraphBuilder.addNodeType(subj, RdfType.CLASS);

        OWLClassExpression supc = sca.getSuperClass();
        if (supc.isAnonymous()) {
            if (supc instanceof OWLObjectSomeValuesFrom) {
                ExistentialRestrictionExpression r = getRestriction(supc);
                if (r == null) {
                    oboGraphBuilder.addUntranslatedAxiom(sca);
                } else {
                    oboGraphBuilder.addEdge(subj, r.propertyId(), r.fillerId(), meta);
                }
            } else if (supc instanceof OWLObjectAllValuesFrom avf) {
                convertObjectAllValuesFrom(sca, oboGraphBuilder, meta, avf, subj);
            }
        } else {
            oboGraphBuilder.addEdge(subj, SUBCLASS_OF, getClassId((OWLClass) supc), meta);
        }
    }

    private void convertObjectAllValuesFrom(OWLSubClassOfAxiom sca, OboGraphBuilder oboGraphBuilder, Meta meta, OWLObjectAllValuesFrom avf, String subj) {
        OWLObjectPropertyExpression property = avf.getProperty();
        switch (property) {
            case OWLObjectProperty owlObjectProperty when avf.getFiller().isNamed() -> {
                String propertyId = getPropertyId(property);
                DomainRangeAxiom domainRangeAxiom = oboGraphBuilder.getDomainRangeAxiomBuilder(propertyId).build();
                Edge edge = buildEdge(subj,
                        //TODO CHECK!!!
                        domainRangeAxiom.predicateId(),
                        getClassId(avf.getFiller().asOWLClass()),
                        meta);
                oboGraphBuilder.addPropertyEdgeDefinitions(propertyId, edge);
            }
            case OWLObjectInverseOf iop when avf.getFiller().isNamed() && iop.isNamed() -> {
                String pid = getPropertyId(iop.getInverse());
                Edge edge = buildEdge(subj, INVERSE_OF, pid, meta);
                oboGraphBuilder.addPropertyEdgeDefinitions(pid, edge);
            }
            default -> oboGraphBuilder.addUntranslatedAxiom(sca);
        }
    }

    private void convertOWLClassAssertionAxiom(OWLClassAssertionAxiom ca, OboGraphBuilder oboGraphBuilder, Meta meta) {
        OWLClassExpression cx = ca.getClassExpression();
        if (cx.isAnonymous()) {
            oboGraphBuilder.addUntranslatedAxiom(ca);
        } else {
            String subj = getIndividualId(ca.getIndividual());
            String obj = getClassId(cx.asOWLClass());
            oboGraphBuilder.addEdge(subj, TYPE, obj, meta);
            oboGraphBuilder.addNodeId(subj); // always include
            oboGraphBuilder.addNodeId(obj); // always include
        }
    }

    private void convertOWLObjectPropertyAssertionAxiom(OWLObjectPropertyAssertionAxiom opa, OboGraphBuilder oboGraphBuilder, Meta meta) {
        String subj = getIndividualId(opa.getSubject());
        String obj = getIndividualId(opa.getObject());
        if (opa.getProperty().isAnonymous()) {
            oboGraphBuilder.addUntranslatedAxiom(opa);
        } else {
            String pred = getPropertyId(opa.getProperty());
            oboGraphBuilder.addEdge(subj, pred, obj, meta);
        }
        // always include subject node of an OPA
        oboGraphBuilder.addNodeId(subj);
    }

    private void convertOWLEquivalentClassesAxiom(OWLEquivalentClassesAxiom eca, Meta meta, OboGraphBuilder oboGraphBuilder) {
        List<OWLClassExpression> xs = eca.getClassExpressionsAsList();
        List<OWLClassExpression> anonXs = xs.stream()
                .filter(IsAnonymous::isAnonymous)
                .toList();
        List<OWLClassExpression> namedXs = xs.stream()
                .filter(IsAnonymous::isNamed)
                .toList();
        if (anonXs.isEmpty()) {
            Set<String> xClassIds = namedXs.stream()
                    .map(x -> getClassId((OWLClass) x))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            // EquivalentNodesSet
            // all classes in equivalence axiom are named
            // TODO: merge pairwise assertions into a clique
            //TODO: EquivalentNodesSet.representativeNodeId() is not set in the production code!!
            EquivalentNodesSet enset = new EquivalentNodesSet.Builder().nodeIds(xClassIds).meta(nullIfEmpty(meta)).build();
            oboGraphBuilder.addEquivalentNodesSet(enset);
        } else {
            // possibilities are:
            //  - LogicalDefinitionAxiom
            //  - TBD
            if (anonXs.size() == 1 && namedXs.size() == 1) {
                OWLClassExpression anonX = anonXs.get(0);
                if (anonX instanceof OWLObjectIntersectionOf owlObjectIntersectionOf) {
                    Set<OWLClassExpression> ixs = owlObjectIntersectionOf.getOperands();
                    OBOClassDef classDef = getClassDef(ixs);
                    if (classDef != null && !classDef.restrs.contains(null)) {
                        LogicalDefinitionAxiom lda = new LogicalDefinitionAxiom.Builder()
                                .definedClassId(getClassId((OWLClass) namedXs.get(0)))
                                .genusIds(classDef.genusClassIds)
                                .restrictions(classDef.restrs)
                                .build();
                        oboGraphBuilder.addLogicalDefinitionAxiom(lda);
                    } else {
                        oboGraphBuilder.addUntranslatedAxiom(eca);
                    }
                }
            }
        }
    }

    private void convertOWLObjectPropertyAxiom(OWLObjectPropertyAxiom owlObjectPropertyAxiom, OboGraphBuilder oboGraphBuilder, Meta meta) {
        switch (owlObjectPropertyAxiom) {
            case OWLSubObjectPropertyOfAxiom spa when spa.getSubProperty().isNamed() && spa.getSuperProperty().isNamed() -> {
                String subj = getPropertyId(spa.getSubProperty());
                String obj = getPropertyId(spa.getSuperProperty());
                oboGraphBuilder.addEdge(subj, SUBPROPERTY_OF, obj, meta);
            }
            case OWLInverseObjectPropertiesAxiom ipa when ipa.getFirstProperty().isNamed() && ipa.getSecondProperty().isNamed() -> {
                String p1 = getPropertyId(ipa.getFirstProperty());
                String p2 = getPropertyId(ipa.getSecondProperty());
                oboGraphBuilder.addEdge(p1, INVERSE_OF, p2, meta);
            }
            case OWLSubPropertyChainOfAxiom spc when spc.getSuperProperty().isNamed() -> {
                String p = getPropertyId(spc.getSuperProperty());
                List<String> cpids = spc.getPropertyChain().stream()
                        .map(cp -> cp.isAnonymous() ? null : getPropertyId(cp))
                        .toList();
                if (cpids.stream().noneMatch(Objects::isNull)) {
                    oboGraphBuilder.addPropertyChainAxiom(new PropertyChainAxiom.Builder().predicateId(p).chainPredicateIds(cpids).build());
                }
            }
            case OWLObjectPropertyRangeAxiom propertyRangeAxiom when propertyRangeAxiom.getRange().isNamed() -> {
                OWLClassExpression rc = propertyRangeAxiom.getRange();
                String propertyId = getPropertyId(propertyRangeAxiom.getProperty());
                oboGraphBuilder.addPropertyRangeClassId(propertyId, getClassId(rc.asOWLClass()));
            }
            case OWLObjectPropertyDomainAxiom propertyDomainAxiom when propertyDomainAxiom.getDomain().isNamed() -> {
                OWLClassExpression rc = propertyDomainAxiom.getDomain();
                String propertyId = getPropertyId(propertyDomainAxiom.getProperty());
                oboGraphBuilder.addPropertyDomainClassId(propertyId, getClassId(rc.asOWLClass()));
            }
            case null, default -> {
                // do nothing
            }
        }
    }

    private void convertOWLAnnotationAssertionAxiom(OWLAnnotationAssertionAxiom aaa, OboGraphBuilder oboGraphBuilder, Meta meta) {
        // NON-LOGICAL AXIOMS
        OWLAnnotationProperty p = aaa.getProperty();
        OWLAnnotationSubject s = aaa.getSubject();

        // non-blank nodes
        if (s instanceof IRI sIri) {
            IRI pIRI = p.getIRI();
            String subj = getNodeId(sIri);

            OWLAnnotationValue v = aaa.getValue();
            String lv = (v instanceof OWLLiteral literal) ? literal.getLiteral() : null;
            if (p.isDeprecated() && aaa.isDeprecatedIRIAssertion()) {
                oboGraphBuilder.setNodeDeprecated(subj, true);
            } else if (isOboInOwlIdProperty(pIRI)) {
                // skip
            } else if (isInSubsetProperty(pIRI)) {
                oboGraphBuilder.addNodeSubset(subj, v.toString());
            } else if (p.isLabel() && lv != null) {
                oboGraphBuilder.addNodeLabel(subj, lv);
            } else if (isDefinitionProperty(pIRI) && lv != null) {
                DefinitionPropertyValue def = new DefinitionPropertyValue.Builder()
                        .val(lv)
                        .xrefs(meta.xrefsValues())
                        .meta(buildBasicPropertyValueMeta(meta))
                        .build();
                oboGraphBuilder.addNodeDefinitionPropertyValue(subj, def);
            } else if (isHasXrefProperty(pIRI) && lv != null) {
                XrefPropertyValue xref = new XrefPropertyValue.Builder()
                        .val(lv)
                        .meta(buildBasicPropertyValueMeta(meta))
                        .build();
                oboGraphBuilder.addNodeXrefPropertyValue(subj, xref);
            } else if (p.isComment() && lv != null) {
                oboGraphBuilder.addNodeComment(subj, lv);
            } else if (SynonymVocabulary.containsIri(pIRI.toString()) && lv != null) {
                Scope scope = SynonymVocabulary.getScope(pIRI.toString());
                String synonymType = "";
                for (OWLAnnotation a : aaa.getAnnotations()) {
                    if (a.getProperty().getIRI().toString().equals(SynonymVocabulary.SYNONYM_TYPE)) {
                        synonymType = a.getValue().toString();
                    } else {
                        // TODO: capture these in meta
                    }
                }
                SynonymPropertyValue syn = new SynonymPropertyValue.Builder()
                        .pred(scope.pred())
                        .synonymType(synonymType)
                        .val(lv)
                        .xrefs(meta.xrefsValues())
                        .meta(buildBasicPropertyValueMeta(meta))
                        .build();
                oboGraphBuilder.addNodeSynonymPropertyValue(subj, syn);
            } else {
                String val = switch (v) {
                    case IRI iri -> iri.toString();
                    case OWLLiteral owlLiteral -> owlLiteral.getLiteral();
                    case OWLAnonymousIndividual owlAnonymousIndividual -> owlAnonymousIndividual.getID().toString();
                    default -> "";
                };
                BasicPropertyValue basicPropertyValue = new BasicPropertyValue.Builder()
                        .pred(getPropertyId(p))
                        .val(val)
                        .meta(buildBasicPropertyValueMeta(meta))
                        .build();
                oboGraphBuilder.addNodeBasicPropertyValue(subj, basicPropertyValue);
            }
        } else {
            // subject is anonymous
            oboGraphBuilder.addUntranslatedAxiom(aaa);
        }
    }

    static class OboGraphBuilder {

        private final String graphId;
        private final Meta meta;

        private final List<Edge> edges = new ArrayList<>();
        private final List<EquivalentNodesSet> ensets = new ArrayList<>();
        private final List<LogicalDefinitionAxiom> ldas = new ArrayList<>();
        private final Set<String> nodeIds = new LinkedHashSet<>();
        private final Map<String, RdfType> nodeTypeMap = new LinkedHashMap<>();
        private final Map<String, String> nodeLabelMap = new LinkedHashMap<>();
        private final Map<String, DomainRangeAxiom.Builder> domainRangeBuilderMap = new LinkedHashMap<>();
        private final List<PropertyChainAxiom> pcas = new ArrayList<>();

        // Each node can be built from multiple axioms; use a builder for each nodeId
        private final Map<String, Meta.Builder> nodeMetaBuilderMap = new LinkedHashMap<>();

        private final Set<OWLAxiom> untranslatedAxioms = new LinkedHashSet<>();
        private final LinkedHashMap<String, PropertyType> nodePropertyTypeMap = new LinkedHashMap<>();

        public OboGraphBuilder(String graphId, @Nullable Meta graphMeta) {
            this.graphId = graphId == null ? "" : graphId;
            this.meta = nullIfEmpty(graphMeta);
        }

        // This method prevents empty 'meta = {}' nodes appearing in the JSON/JYAML output
        private @Nullable Meta nullIfEmpty(@Nullable Meta meta) {
            return NodeOrEdge.EMPTY_META.equals(meta) ? null : meta;
        }

        public void addNodeId(String nodeId) {
            nodeIds.add(nodeId);
        }

        public void addNodeType(String nodeId, RdfType rdfType) {
            // ensure all nodes are added, even if they lack a label
            nodeIds.add(nodeId);
            nodeTypeMap.put(nodeId, rdfType);
        }

        public void addNodeType(String nodeId, PropertyType propertyType) {
            nodeIds.add(nodeId);
            nodeTypeMap.put(nodeId, RdfType.PROPERTY);
            nodePropertyTypeMap.put(nodeId, propertyType);
        }

        public void addNodeLabel(String nodeId, String label) {
            nodeIds.add(nodeId);
            nodeLabelMap.put(nodeId, label);
        }

        public void addEdge(Edge edge) {
            if (edge != null) {
                edges.add(edge);
            }
        }

        public void addEdge(String subj, String pred, String obj, @Nullable Meta meta) {
            edges.add(buildEdge(subj, pred, obj, meta));
        }

        private Edge buildEdge(String subj, String pred, String obj, @Nullable Meta meta) {
            if (meta == null || NodeOrEdge.EMPTY_META.equals(meta)) {
                return new Edge.Builder().sub(subj).pred(pred).obj(obj).build();
            }
            return new Edge.Builder().sub(subj).pred(pred).obj(obj).meta(meta).build();
        }

        public void addNodeDefinitionPropertyValue(String nodeId, DefinitionPropertyValue def) {
            Meta.Builder nb = getMetaBuilderForId(nodeId);
            nb.definition(def);
            nodeIds.add(nodeId);
        }

        public void addNodeXrefPropertyValue(String nodeId, XrefPropertyValue xrefPropertyValue) {
            Meta.Builder nb = getMetaBuilderForId(nodeId);
            nb.addXref(xrefPropertyValue);
            nodeIds.add(nodeId);
        }

        public void setNodeDeprecated(String nodeId, boolean isDeprecated) {
            Meta.Builder nb = getMetaBuilderForId(nodeId);
            nb.deprecated(isDeprecated);
            nodeIds.add(nodeId);
        }

        public void addNodeComment(String nodeId, String comment) {
            Meta.Builder nb = getMetaBuilderForId(nodeId);
            nb.addComment(comment);
            nodeIds.add(nodeId);
        }

        public void addNodeSubset(String nodeId, String subset) {
            Meta.Builder nb = getMetaBuilderForId(nodeId);
            nb.addSubset(subset);
            nodeIds.add(nodeId);
        }

        public void addNodeSynonymPropertyValue(String nodeId, SynonymPropertyValue syn) {
            Meta.Builder nb = getMetaBuilderForId(nodeId);
            nb.addSynonym(syn);
            nodeIds.add(nodeId);
        }

        public void addNodeBasicPropertyValue(String nodeId, BasicPropertyValue basicPropertyValue) {
            Meta.Builder nb = getMetaBuilderForId(nodeId);
            nb.addBasicPropertyValue(basicPropertyValue);
            nodeIds.add(nodeId);
        }

        private Meta.Builder getMetaBuilderForId(String id) {
            return nodeMetaBuilderMap.computeIfAbsent(id, k -> new Meta.Builder());
        }

        public void addUntranslatedAxiom(OWLAxiom owlAxiom) {
            untranslatedAxioms.add(owlAxiom);
        }

        public void addPropertyEdgeDefinitions(String propertyId, Edge edge) {
            DomainRangeAxiom.Builder b = getDomainRangeAxiomBuilder(propertyId);
            b.addAllValuesFromEdge(edge);
        }

        public void addPropertyDomainClassId(String propertyId, String domainClassId) {
            DomainRangeAxiom.Builder b = getDomainRangeAxiomBuilder(propertyId);
            b.addDomainClassId(domainClassId);
        }


        public void addPropertyRangeClassId(String propertyId, String rangeClassId) {
            DomainRangeAxiom.Builder b = getDomainRangeAxiomBuilder(propertyId);
            b.addRangeClassId(rangeClassId);
        }

        private DomainRangeAxiom.Builder getDomainRangeAxiomBuilder(String propertyId) {
            return domainRangeBuilderMap.computeIfAbsent(propertyId, id -> new DomainRangeAxiom.Builder().predicateId(id));
        }

        public void addLogicalDefinitionAxiom(LogicalDefinitionAxiom logicalDefinitionAxiom) {
            ldas.add(logicalDefinitionAxiom);
        }

        public void addPropertyChainAxiom(PropertyChainAxiom propertyChainAxiom) {
            pcas.add(propertyChainAxiom);
        }

        public void addEquivalentNodesSet(EquivalentNodesSet equivalentNodesSet) {
            ensets.add(equivalentNodesSet);
        }

        public List<OWLAxiom> untranslatedAxioms() {
            return List.copyOf(untranslatedAxioms);
        }

        public Graph buildGraph() {
            List<Node> nodes = new ArrayList<>();
            for (String n : nodeIds) {
                var nb = new Node.Builder()
                        .id(n)
                        .label(nodeLabelMap.getOrDefault(n, ""));
                if (nodeMetaBuilderMap.containsKey(n)) {
                    Meta nodeMeta = nodeMetaBuilderMap.get(n).build();
                    nb.meta(nullIfEmpty(nodeMeta));
                }
                if (nodeTypeMap.containsKey(n)) {
                    RdfType type = nodeTypeMap.get(n);
                    nb.rdfType(type);
                    if (type == RdfType.PROPERTY) {
                        // Change for https://github.com/geneontology/obographs/issues/65
                        PropertyType propertyType = nodePropertyTypeMap.get(n);
                        nb.propertyType(propertyType);
                    }
                }
                nodes.add(nb.build());
            }

            List<DomainRangeAxiom> domainRangeAxioms = domainRangeBuilderMap.values()
                    .stream()
                    .map(DomainRangeAxiom.Builder::build)
                    .toList();

            return new Graph.Builder()
                    .id(graphId)
                    .meta(meta)
                    .nodes(nodes)
                    .edges(edges)
                    .equivalentNodesSets(ensets)
                    .logicalDefinitionAxioms(ldas)
                    .domainRangeAxioms(domainRangeAxioms)
                    .propertyChainAxioms(pcas)
                    .build();
        }
    }


    /**
     * Translate all axiom annotations into a Meta object
     *
     * @param ax
     * @return
     */
    private Meta buildMeta(OWLAxiom ax) {
        return buildMeta(ax.getAnnotations());
    }

    private Meta buildMeta(Set<OWLAnnotation> anns) {
        return buildMeta(anns, null);
    }

    private Meta buildMeta(Set<OWLAnnotation> anns, @Nullable String version) {
        Meta.Builder builder = new Meta.Builder();
        for (OWLAnnotation ann : anns) {
            OWLAnnotationProperty p = ann.getProperty();
            OWLAnnotationValue v = ann.getValue();
            String val = v instanceof IRI iri ? iri.toString() : ((OWLLiteral) v).getLiteral();
            if (ann.isDeprecatedIRIAnnotation()) {
                builder.deprecated(true);
            } else if (isHasXrefProperty(p.getIRI())) {
                builder.addXref(new XrefPropertyValue.Builder().val(val).build());
            } else if (isExactMatchProperty(p.getIRI())) {
                builder.addXref(new XrefPropertyValue.Builder().val(val).pred("exactMatch").build());
            } else if (isInSubsetProperty(p.getIRI())) {
                builder.addSubset(val);
            } else if (isHasSynonymTypeProperty(p.getIRI())) {
                builder.addSubset(val);
            } else {
                builder.addBasicPropertyValue(new BasicPropertyValue.Builder()
                        .pred(getPropertyId(p))
                        .val(val)
                        .build());
            }
        }
        if (version != null) {
            builder.version(version);
        }
        return builder.build();
    }

    private Meta buildBasicPropertyValueMeta(Meta existingMeta) {
        List<BasicPropertyValue> basicPropertyValues = existingMeta.basicPropertyValues();
        return basicPropertyValues.isEmpty() ? null : new Meta.Builder()
                .addAllBasicPropertyValues(basicPropertyValues)
                .build();
    }

    private Meta nullIfEmpty(Meta meta) {
        return NodeOrEdge.EMPTY_META.equals(meta) ? null : meta;
    }


    private Edge buildEdge(String subj, String pred, String obj, @Nullable Meta meta) {
        return buildEdge(subj, pred, obj, meta, null);
    }


    private Edge buildEdge(String subj, String pred, String obj, @Nullable Meta meta, List<ExistentialRestrictionExpression> gciQualifiers) {
        if (NodeOrEdge.EMPTY_META.equals(meta)) {
            return new Edge.Builder().sub(subj).pred(pred).obj(obj).build();
        }
        return new Edge.Builder().sub(subj).pred(pred).obj(obj).meta(meta).build();
    }

    @Nullable
    private ExistentialRestrictionExpression getRestriction(OWLClassExpression x) {
        if (x instanceof OWLObjectSomeValuesFrom r) {
            OWLPropertyExpression p = r.getProperty();
            OWLClassExpression f = r.getFiller();
            if (p instanceof OWLObjectProperty owlObjectProperty && !f.isAnonymous()) {
                return new ExistentialRestrictionExpression.Builder()
                        .propertyId(getPropertyId(owlObjectProperty))
                        .fillerId(getClassId((OWLClass) f))
                        .build();
            }
//            if (p instanceof OWLObjectProperty && f instanceof OWLObjectSomeValuesFrom) {
//                System.out.println("Nested OWLObjectSomeValuesFrom " + x);
//                return new ExistentialRestrictionExpression.Builder()
//                        .propertyId(getPropertyId((OWLObjectProperty) p))
//                        .fillerId(getClassId(((OWLObjectSomeValuesFrom) f).getFiller().asOWLClass()))
//                        .build();
//            }
            // n.b nested OWLObjectSomeValuesFrom will be removed from the output. These can be found by testing that
            //  f instanceof OWLObjectSomeValuesFrom
        }
        return null;
    }

    @Nullable
    private OBOClassDef getClassDef(Set<OWLClassExpression> ixs) {
        OBOClassDef def = new OBOClassDef();
        boolean isLDA = true;
        for (OWLClassExpression ix : ixs) {
            if (!ix.isAnonymous()) {
                def.genusClassIds.add(getClassId((OWLClass) ix));
            } else if (ix instanceof OWLObjectSomeValuesFrom) {
                ExistentialRestrictionExpression restriction = getRestriction(ix);
                if (restriction != null) {
                    def.restrs.add(restriction);
                }
            } else {
                isLDA = false;
                break;
            }
        }

        return isLDA && !def.restrs.contains(null) ? def : null;
    }

    private String getPropertyId(OWLObjectPropertyExpression owlObjectPropertyExpression) {
        return getPropertyId(owlObjectPropertyExpression.asOWLObjectProperty());
    }

    private String getPropertyId(OWLObjectProperty p) {
        return p.getIRI().toString();
    }

    private String getPropertyId(OWLProperty p) {
        return p.getIRI().toString();
    }

    private String getClassId(OWLClass c) {
        return c.getIRI().toString();
    }

    private String getIndividualId(OWLIndividual owlIndividual) {
        if (owlIndividual instanceof OWLNamedIndividual)
            return owlIndividual.asOWLNamedIndividual().getIRI().toString();
        else
            return owlIndividual.asOWLAnonymousIndividual().getID().toString(); // TODO - document blank nodes
    }

    /**
     * TODO: optionally compact the IRI using a prefix map
     *
     * @param s
     * @return Id or IRI
     */
    private String getNodeId(IRI s) {
        return s.toString();
    }

    public boolean isDefinitionProperty(IRI iri) {
        return iri.toString().equals("http://purl.obolibrary.org/obo/IAO_0000115");
    }

    public boolean isHasXrefProperty(IRI iri) {
        return iri.toString().equals("http://www.geneontology.org/formats/oboInOwl#hasDbXref");
    }

    public boolean isExactMatchProperty(IRI iri) {
        return iri.toString().equals("http://www.w3.org/2004/02/skos/core#exactMatch");
    }

    public boolean isRelatedMatchProperty(IRI iri) {
        return iri.toString().equals("http://www.w3.org/2004/02/skos/core#relatedMatch");
    }

    public boolean isInSubsetProperty(IRI iri) {
        return iri.toString().equals("http://www.geneontology.org/formats/oboInOwl#inSubset");
    }

    public boolean isHasSynonymTypeProperty(IRI iri) {
        return iri.toString().equals("http://www.geneontology.org/formats/oboInOwl#hasSynonymType");
    }

    public boolean isOboInOwlIdProperty(IRI iri) {
        return iri.toString().equals("http://www.geneontology.org/formats/oboInOwl#id");
    }

}
