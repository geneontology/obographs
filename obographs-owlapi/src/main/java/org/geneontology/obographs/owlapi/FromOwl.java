package org.geneontology.obographs.owlapi;

import com.github.jsonldjava.core.Context;
import org.geneontology.obographs.core.model.AbstractNode.RDFTYPES;
import org.geneontology.obographs.core.model.*;
import org.geneontology.obographs.core.model.Node.Builder;
import org.geneontology.obographs.core.model.axiom.*;
import org.geneontology.obographs.core.model.meta.AbstractSynonymPropertyValue.SCOPES;
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

import org.geneontology.obographs.core.model.AbstractNode.PropertyType;

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

        SynonymVocabulary synonymVocabulary = new SynonymVocabulary();


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

            if (ax instanceof OWLDeclarationAxiom) {
                OWLDeclarationAxiom dax = ((OWLDeclarationAxiom) ax);
                OWLEntity e = dax.getEntity();
                String id = e.getIRI().toString();
                if (e instanceof OWLClass) {
                    oboGraphBuilder.addNodeType(id, RDFTYPES.CLASS);
                } else if (e instanceof OWLDataProperty) {
                    oboGraphBuilder.addNodeType(id, PropertyType.DATA);
                } else if (e instanceof OWLObjectProperty) {
                    oboGraphBuilder.addNodeType(id, PropertyType.OBJECT);
                } else if (e instanceof OWLAnnotationProperty) {
                    oboGraphBuilder.addNodeType(id, PropertyType.ANNOTATION);
                } else if (e instanceof OWLNamedIndividual) {
                    oboGraphBuilder.addNodeType(id, RDFTYPES.INDIVIDUAL);
                }
            } else if (ax instanceof OWLLogicalAxiom) {
                // LOGICAL AXIOMS

                if (ax instanceof OWLSubClassOfAxiom) {
                    // SUBCLASS

                    OWLSubClassOfAxiom sca = (OWLSubClassOfAxiom) ax;
                    OWLClassExpression subc = sca.getSubClass();
                    OWLClassExpression supc = sca.getSuperClass();

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
                        String subj = getClassId((OWLClass) subc);

                        oboGraphBuilder.addNodeType(subj, RDFTYPES.CLASS);

                        if (supc.isAnonymous()) {
                            if (supc instanceof OWLObjectSomeValuesFrom) {
                                ExistentialRestrictionExpression r = getRestriction(supc);
                                if (r == null) {
                                    oboGraphBuilder.addUntranslatedAxiom(sca);
                                } else {
                                    oboGraphBuilder.addEdge(subj, r.getPropertyId(), r.getFillerId(), meta);
                                }
                            } else if (supc instanceof OWLObjectAllValuesFrom) {
                                OWLObjectAllValuesFrom avf = (OWLObjectAllValuesFrom) supc;
                                OWLObjectPropertyExpression property = avf.getProperty();
                                if (property instanceof OWLObjectProperty) {
                                    if (avf.getFiller().isNamed()) {
                                        String propertyId = getPropertyId(property);
                                        DomainRangeAxiom domainRangeAxiom = oboGraphBuilder.getDomainRangeAxiomBuilder(propertyId).build();
                                        Edge edge = buildEdge(subj,
                                                //TODO CHECK!!!
                                                domainRangeAxiom.getPredicateId(),
                                                getClassId(avf.getFiller().asOWLClass()),
                                                meta);
                                        oboGraphBuilder.addPropertyEdgeDefinitions(propertyId, edge);
                                    } else {
                                        oboGraphBuilder.addUntranslatedAxiom(sca);
                                    }
                                } else if (property instanceof OWLObjectInverseOf) {
                                    OWLObjectInverseOf iop = (OWLObjectInverseOf) property;
                                    if (avf.getFiller().isNamed() && iop.isNamed()) {
                                        String pid = getPropertyId(iop.getInverse());
                                        Edge edge = buildEdge(subj, INVERSE_OF, pid, meta);
                                        oboGraphBuilder.addPropertyEdgeDefinitions(pid, edge);
                                    } else {
                                        oboGraphBuilder.addUntranslatedAxiom(sca);
                                    }
                                }
                            }
                        } else {
                            oboGraphBuilder.addEdge(subj, SUBCLASS_OF, getClassId((OWLClass) supc), meta);
                        }
                    } else {
                        // Logically impossible to reach this? subj is either named or anonymous
                        oboGraphBuilder.addUntranslatedAxiom(sca);
                    }
                } else if (ax instanceof OWLClassAssertionAxiom) {
                    OWLClassAssertionAxiom ca = (OWLClassAssertionAxiom) ax;

                    String subj = getIndividualId(ca.getIndividual());
                    String obj;
                    OWLClassExpression cx = ca.getClassExpression();
                    if (cx.isAnonymous()) {
                        oboGraphBuilder.addUntranslatedAxiom(ca);
                        continue;
                    } else {
                        obj = getClassId(cx.asOWLClass());
                    }
                    oboGraphBuilder.addEdge(subj, TYPE, obj, meta);
                    oboGraphBuilder.addNodeId(subj); // always include
                    oboGraphBuilder.addNodeId(obj); // always include

                } else if (ax instanceof OWLObjectPropertyAssertionAxiom) {
                    OWLObjectPropertyAssertionAxiom opa = (OWLObjectPropertyAssertionAxiom) ax;

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
                } else if (ax instanceof OWLEquivalentClassesAxiom) {
                    // EQUIVALENT
                    OWLEquivalentClassesAxiom eca = (OWLEquivalentClassesAxiom) ax;
                    List<OWLClassExpression> xs = eca.getClassExpressionsAsList();
                    List<OWLClassExpression> anonXs = xs.stream()
                            .filter(IsAnonymous::isAnonymous)
                            .collect(Collectors.toUnmodifiableList());
                    List<OWLClassExpression> namedXs = xs.stream()
                            .filter(IsAnonymous::isNamed)
                            .collect(Collectors.toUnmodifiableList());
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
                            if (anonX instanceof OWLObjectIntersectionOf) {

                                Set<OWLClassExpression> ixs = ((OWLObjectIntersectionOf) anonX).getOperands();

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
                } else if (ax instanceof OWLObjectPropertyAxiom) {
                    if (ax instanceof OWLSubObjectPropertyOfAxiom) {
                        OWLSubObjectPropertyOfAxiom spa = (OWLSubObjectPropertyOfAxiom) ax;
                        if (spa.getSubProperty().isNamed() && spa.getSuperProperty().isNamed()) {
                            String subj = getPropertyId(spa.getSubProperty());
                            String obj = getPropertyId(spa.getSuperProperty());
                            oboGraphBuilder.addEdge(subj, SUBPROPERTY_OF, obj, meta);
                        }

                    } else if (ax instanceof OWLInverseObjectPropertiesAxiom) {
                        OWLInverseObjectPropertiesAxiom ipa = (OWLInverseObjectPropertiesAxiom) ax;
                        if (ipa.getFirstProperty().isNamed() && ipa.getSecondProperty().isNamed()) {
                            String p1 = getPropertyId(ipa.getFirstProperty());
                            String p2 = getPropertyId(ipa.getSecondProperty());
                            oboGraphBuilder.addEdge(p1, INVERSE_OF, p2, meta);
                        }
                    } else if (ax instanceof OWLSubPropertyChainOfAxiom) {
                        OWLSubPropertyChainOfAxiom spc = (OWLSubPropertyChainOfAxiom) ax;
                        if (spc.getSuperProperty().isNamed()) {
                            String p = getPropertyId(spc.getSuperProperty());
                            List<String> cpids = spc.getPropertyChain().stream()
                                    .map(cp -> cp.isAnonymous() ? null : getPropertyId(cp))
                                    .collect(Collectors.toList());
                            if (cpids.stream().noneMatch(Objects::isNull)) {
                                oboGraphBuilder.addPropertyChainAxiom(new PropertyChainAxiom.Builder().predicateId(p).chainPredicateIds(cpids).build());
                            }
                        }
                    } else if (ax instanceof OWLObjectPropertyRangeAxiom) {
                        OWLObjectPropertyRangeAxiom rax = (OWLObjectPropertyRangeAxiom) ax;
                        OWLClassExpression rc = rax.getRange();
                        if (rc.isNamed()) {
                            String propertyId = getPropertyId(rax.getProperty());
                            oboGraphBuilder.addPropertyRangeClassId(propertyId, getClassId(rc.asOWLClass()));
                        }
                    } else if (ax instanceof OWLObjectPropertyDomainAxiom) {
                        OWLObjectPropertyDomainAxiom rax = (OWLObjectPropertyDomainAxiom) ax;
                        OWLClassExpression rc = rax.getDomain();
                        if (rc.isNamed()) {
                            String propertyId = getPropertyId(rax.getProperty());
                            oboGraphBuilder.addPropertyDomainClassId(propertyId, getClassId(rc.asOWLClass()));
                        }
                    }
                } else {
                    oboGraphBuilder.addUntranslatedAxiom(ax);
                }
            } else {
                // NON-LOGICAL AXIOMS
                if (ax instanceof OWLAnnotationAssertionAxiom) {
                    OWLAnnotationAssertionAxiom aaa = (OWLAnnotationAssertionAxiom) ax;
                    OWLAnnotationProperty p = aaa.getProperty();
                    OWLAnnotationSubject s = aaa.getSubject();

                    // non-blank nodes
                    if (s instanceof IRI) {
                        IRI pIRI = p.getIRI();
                        String subj = getNodeId((IRI) s);

                        OWLAnnotationValue v = aaa.getValue();
                        String lv = null;
                        if (v instanceof OWLLiteral) {
                            lv = ((OWLLiteral) v).getLiteral();
                        }
                        if (p.isLabel() && lv != null) {
                            oboGraphBuilder.addNodeLabel(subj, lv);
                        } else if (isDefinitionProperty(pIRI) && lv != null) {
                            DefinitionPropertyValue def = new DefinitionPropertyValue.Builder()
                                    .val(lv)
                                    .xrefs(meta.getXrefsValues())
                                    .meta(buildBasicPropertyValueMeta(meta))
                                    .build();
                            oboGraphBuilder.addNodeDefinitionPropertyValue(subj, def);
                        } else if (isHasXrefProperty(pIRI) && lv != null) {
                            XrefPropertyValue xref = new XrefPropertyValue.Builder()
                                    .val(lv)
                                    .meta(buildBasicPropertyValueMeta(meta))
                                    .build();
                            oboGraphBuilder.addNodeXrefPropertyValue(subj, xref);
                        } else if (p.isDeprecated() && aaa.isDeprecatedIRIAssertion()) {
                            oboGraphBuilder.setNodeDeprecated(subj, true);
                        } else if (p.isComment() && lv != null) {
                            oboGraphBuilder.addNodeComment(subj, lv);
                        } else if (isOboInOwlIdProperty(pIRI)) {
                            // skip
                        } else if (isInSubsetProperty(pIRI)) {
                            oboGraphBuilder.addNodeSubset(subj, v.toString());
                        } else if (synonymVocabulary.contains(pIRI.toString()) && lv != null) {
                            SCOPES scope = synonymVocabulary.get(pIRI.toString());
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
                                    .xrefs(meta.getXrefsValues())
                                    .meta(buildBasicPropertyValueMeta(meta))
                                    .build();
                            oboGraphBuilder.addNodeSynonymPropertyValue(subj, syn);
                        } else {
                            String val;
                            if (v instanceof IRI) {
                                val = ((IRI) v).toString();
                            } else if (v instanceof OWLLiteral) {
                                val = ((OWLLiteral) v).getLiteral();
                            } else if (v instanceof OWLAnonymousIndividual) {
                                val = ((OWLAnonymousIndividual) v).getID().toString();
                            } else {
                                val = "";
                            }

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

    static class OboGraphBuilder {

        private final String graphId;
        private final Meta meta;

        private final List<Edge> edges = new ArrayList<>();
        private final List<EquivalentNodesSet> ensets = new ArrayList<>();
        private final List<LogicalDefinitionAxiom> ldas = new ArrayList<>();
        private final Set<String> nodeIds = new LinkedHashSet<>();
        private final Map<String, RDFTYPES> nodeTypeMap = new LinkedHashMap<>();
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

        public void addNodeType(String nodeId, RDFTYPES rdftypes) {
            // ensure all nodes are added, even if they lack a label
            nodeIds.add(nodeId);
            nodeTypeMap.put(nodeId, rdftypes);
        }

        public void addNodeType(String nodeId, PropertyType propertyType) {
            nodeIds.add(nodeId);
            nodeTypeMap.put(nodeId, RDFTYPES.PROPERTY);
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
            nodeIds.add(nodeId); // TODO: CHECK!! NEW ADDITION in 0.3.1
        }

        public void addNodeComment(String nodeId, String comment) {
            Meta.Builder nb = getMetaBuilderForId(nodeId);
            nb.addComment(comment);
            nodeIds.add(nodeId); // TODO: CHECK!! NEW ADDITION in 0.3.1
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
                Builder nb = new Node.Builder()
                        .id(n)
                        .label(nodeLabelMap.getOrDefault(n, ""));
                if (nodeMetaBuilderMap.containsKey(n)) {
                    Meta nodeMeta = nodeMetaBuilderMap.get(n).build();
                    nb.meta(nullIfEmpty(nodeMeta));
                }
                if (nodeTypeMap.containsKey(n)) {
                    RDFTYPES type = nodeTypeMap.get(n);
                    nb.type(type);
                    if (type == RDFTYPES.PROPERTY) {
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
                    .collect(Collectors.toList());

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
            String val = v instanceof IRI ? ((IRI) v).toString() : ((OWLLiteral) v).getLiteral();
            if (ann.isDeprecatedIRIAnnotation()) {
                builder.deprecated(true);
            } else if (isHasXrefProperty(p.getIRI())) {
                builder.addXref(new XrefPropertyValue.Builder().val(val).build());
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
        List<BasicPropertyValue> basicPropertyValues = existingMeta.getBasicPropertyValues();
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
        if (x instanceof OWLObjectSomeValuesFrom) {
            OWLObjectSomeValuesFrom r = (OWLObjectSomeValuesFrom) x;
            OWLPropertyExpression p = r.getProperty();
            OWLClassExpression f = r.getFiller();
            if (p instanceof OWLObjectProperty && !f.isAnonymous()) {
                return new ExistentialRestrictionExpression.Builder()
                        .propertyId(getPropertyId((OWLObjectProperty) p))
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
