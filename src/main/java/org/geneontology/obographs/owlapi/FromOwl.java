package org.geneontology.obographs.owlapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.geneontology.obographs.io.PrefixHelper;
import org.geneontology.obographs.model.Edge;
import org.geneontology.obographs.model.Graph;
import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.model.Meta;
import org.geneontology.obographs.model.Node;
import org.geneontology.obographs.model.Node.Builder;
import org.geneontology.obographs.model.Node.RDFTYPES;
import org.geneontology.obographs.model.axiom.EquivalentNodesSet;
import org.geneontology.obographs.model.axiom.ExistentialRestrictionExpression;
import org.geneontology.obographs.model.axiom.LogicalDefinitionAxiom;
import org.geneontology.obographs.model.meta.DefinitionPropertyValue;
import org.geneontology.obographs.model.meta.SynonymPropertyValue;
import org.geneontology.obographs.model.meta.SynonymPropertyValue.SCOPES;
import org.geneontology.obographs.model.meta.XrefPropertyValue;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLLogicalAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.jsonldjava.core.Context;


/**
 * Implements OWL to OG translation
 * ===
 * 
 * See <a href="https://github.com/geneontology/obographs/blob/master/README-owlmapping.md">OWL Mapping spec</a>
 * 
 * <br/>
 * Status: _currently incomplete_
 * <br/>
 * 
 * @see "[SPEC](https://github.com/geneontology/obographs/blob/master/README-owlmapping.md)"
 * 
 * TODO:
 *  * Generate Meta objects
 *  * Synonyms
 * 
 * @author cjm
 *
 */
public class FromOwl {

    public static final String SUBCLASS_OF = "is_a";

    private PrefixHelper prefixHelper;
    private Context context;

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
            graphs.add( generateGraph(ont));
        }
        return new GraphDocument.Builder().graphs(graphs).build();
    }

    /**
     * @param ontology
     * @return Graph generated from ontology
     */
    public Graph generateGraph(OWLOntology ontology) {
        
        SynonymVocabulary synonymVocabulary = new SynonymVocabulary();

        List<Edge> edges = new ArrayList<>();
        List<Node> nodes = new ArrayList<>();
        List<EquivalentNodesSet> ensets = new ArrayList<>();
        List<LogicalDefinitionAxiom> ldas = new ArrayList<>();
        Set<String> nodeIds = new HashSet<>();
        Map<String,RDFTYPES> nodeTypeMap = new HashMap<>();
        Map<String,String> nodeLabelMap = new HashMap<>();

        // Each node can be built from multiple axioms; use a builder for each nodeId
        Map<String,Meta.Builder> nodeMetaBuilderMap = new HashMap<>();


        Set<OWLAxiom> untranslatedAxioms = new HashSet<>();

        // iterate over all axioms and push to relevant builders
        for (OWLAxiom ax : ontology.getAxioms()) {

            Meta meta = getAnnotations(ax);

            if (ax instanceof OWLDeclarationAxiom) {
                OWLDeclarationAxiom dax = ((OWLDeclarationAxiom)ax);
                OWLEntity e = dax.getEntity();
                if (e instanceof OWLClass) {
                    setNodeType(getClassId((OWLClass) e), RDFTYPES.CLASS, nodeTypeMap);
                }
            }
            else if (ax instanceof OWLLogicalAxiom) {

                if (ax instanceof OWLSubClassOfAxiom) {
                    // SUBCLASS

                    OWLSubClassOfAxiom sca = (OWLSubClassOfAxiom)ax;
                    OWLClassExpression subc = sca.getSubClass();
                    OWLClassExpression supc = sca.getSuperClass();
                    if (subc.isAnonymous()) {
                        untranslatedAxioms.add(sca);
                    }
                    else {
                        String subj = getClassId((OWLClass) subc);
                        setNodeType(subj, RDFTYPES.CLASS, nodeTypeMap);

                        if (supc.isAnonymous()) {
                            ExistentialRestrictionExpression r = getRestriction(supc);
                            edges.add(getEdge(subj, r.getPropertyId(), r.getFillerId()));
                        }
                        else {
                            edges.add(getEdge(subj, SUBCLASS_OF, getClassId((OWLClass) supc)));

                        }
                    }
                }
                else if (ax instanceof OWLEquivalentClassesAxiom) {
                    // EQUIVALENT

                    OWLEquivalentClassesAxiom eca = (OWLEquivalentClassesAxiom)ax;
                    List<OWLClassExpression> xs = eca.getClassExpressionsAsList();
                    List<OWLClassExpression> anonXs = 
                            xs.stream().filter(x -> x.isAnonymous()).collect(Collectors.toList());
                    List<OWLClassExpression> namedXs = 
                            xs.stream().filter(x -> !x.isAnonymous()).collect(Collectors.toList());
                    Set<String> xClassIds = 
                            namedXs.stream().map(x -> getClassId((OWLClass)x)).collect(Collectors.toSet());
                    if (anonXs.size() == 0) {
                        // EquivalentNodesSet

                        // all classes in equivalence axiom are named
                        // TODO: merge pairwise assertions into a clique
                        EquivalentNodesSet enset = 
                                new EquivalentNodesSet.Builder().nodeIds(xClassIds).build();
                        ensets.add(enset);
                    }
                    else {
                        if (anonXs.size() == 1 && namedXs.size() == 1) {

                            OWLClassExpression anonX = anonXs.get(0);
                            if (anonX instanceof OWLObjectIntersectionOf) {
                                // LDA

                                Set<OWLClassExpression> ixs =
                                        ((OWLObjectIntersectionOf)anonX).getOperands();

                                List<String> genusClassIds = new ArrayList<>();
                                List<ExistentialRestrictionExpression> restrs = new ArrayList<>();
                                boolean isLDA = true;
                                for (OWLClassExpression ix : ixs) {
                                    if (!ix.isAnonymous()) {
                                        genusClassIds.add(getClassId((OWLClass)ix));
                                    }
                                    else if (ix instanceof OWLObjectSomeValuesFrom) {
                                        restrs.add(getRestriction(ix));
                                    }
                                    else {
                                        isLDA = false;
                                        break;
                                    }

                                }

                                if (isLDA) {
                                    LogicalDefinitionAxiom lda =
                                            new LogicalDefinitionAxiom.Builder().
                                            definedClassId(getClassId((OWLClass) namedXs.get(0))).
                                            genusIds(genusClassIds).
                                            restrictions(restrs).
                                            build();
                                    ldas.add(lda);
                                }
                            }
                        }
                        else {

                        }
                    }
                }
                else {
                    untranslatedAxioms.add(ax);
                }
            }
            else {
                // NON-LOGICAL AXIOMS
                if (ax instanceof OWLAnnotationAssertionAxiom) {
                    OWLAnnotationAssertionAxiom aaa = (OWLAnnotationAssertionAxiom)ax;
                    OWLAnnotationProperty p = aaa.getProperty();
                    OWLAnnotationSubject s = aaa.getSubject();
                    if (s instanceof IRI) {

                        String subj = getNodeId((IRI)s);

                        OWLAnnotationValue v = aaa.getValue();
                        String lv = null;
                        if (v instanceof OWLLiteral) {
                            lv = ((OWLLiteral)v).getLiteral();
                        }
                        IRI pIRI = p.getIRI();
                        if (p.isLabel()) {
                            if (lv != null) {
                                nodeIds.add(subj);
                                nodeLabelMap.put(subj, lv);
                            }
                        }
                        else if (isDefinitionProperty(pIRI)) {
                            if (lv != null) {
                                DefinitionPropertyValue def = 
                                        new DefinitionPropertyValue.Builder().
                                        val(lv).
                                        xrefs(meta.getXrefsValues()).
                                        build();

                                Meta.Builder nb = put(nodeMetaBuilderMap, subj);
                                nb.definition(def);
                                nodeIds.add(subj);
                            }

                        }
                        else if (isHasXrefProperty(pIRI)) {
                            if (lv != null) {
                                XrefPropertyValue xref = 
                                        new XrefPropertyValue.Builder().
                                        val(lv).build();

                                Meta.Builder nb = put(nodeMetaBuilderMap, subj);
                                nb.addXref(xref);
                                nodeIds.add(subj);
                            }

                        }
                        else if (synonymVocabulary.contains(pIRI.toString())) {
                            //System.err.println(aaa);
                            SCOPES scope = synonymVocabulary.get(pIRI.toString());
                            //System.err.println(pIRI+" --> "+scope);
                            if (lv != null) {
                                SynonymPropertyValue syn = new SynonymPropertyValue.Builder().
                                        scope(scope).
                                        val(lv).
                                        xrefs(meta.getXrefsValues()).
                                        build();
                                Meta.Builder nb = put(nodeMetaBuilderMap, subj);
                                nb.addSynonym(syn);
                                nodeIds.add(subj);
                            }
                        }
                        else {
                            untranslatedAxioms.add(aaa);
                        }

                    }
                    else {
                        // subject is anonymous
                        untranslatedAxioms.add(aaa);
                    }

                }
            }
        }

        for (String n : nodeIds) {
            Builder nb = new Node.Builder().
                    id(n).
                    label(nodeLabelMap.get(n));
            if (nodeMetaBuilderMap.containsKey(n)) {
                Meta meta = nodeMetaBuilderMap.get(n).build();
                nb.meta(meta);
            }
            if (nodeTypeMap.containsKey(n)) {
                nb.type(nodeTypeMap.get(n));
            }
            nodes.add(nb.build());
        }
        return new Graph.Builder().
                nodes(nodes).
                edges(edges).
                equivalentNodesSet(ensets).
                logicalDefinitionAxioms(ldas).
                build();
    }


    private void setNodeType(String id, RDFTYPES t,
            Map<String, RDFTYPES> nodeTypeMap) {
        nodeTypeMap.put(id, t);
    }

    private Meta.Builder put(Map<String, Meta.Builder> nodeMetaBuilderMap, String id) {
        if (!nodeMetaBuilderMap.containsKey(id))
            nodeMetaBuilderMap.put(id, new Meta.Builder());
        return nodeMetaBuilderMap.get(id);
    }

    /**
     * Translate all axiom annotations into a Meta object
     * 
     * @param ax
     * @return
     */
    private Meta getAnnotations(OWLAxiom ax) {
        List<XrefPropertyValue> xrefs = new ArrayList<>();
        for (OWLAnnotation ann : ax.getAnnotations()) {
            OWLAnnotationProperty p = ann.getProperty();
            OWLAnnotationValue v = ann.getValue();
            if (isHasXrefProperty(p.getIRI())) {
                String val = v instanceof IRI ? ((IRI)v).toString() : ((OWLLiteral)v).getLiteral();
                xrefs.add(new XrefPropertyValue.Builder().val(val).build());
            }
            else {
                // TODO
            }
        }
        return new Meta.Builder().xrefs(xrefs).build();
    }



    private Edge getEdge(String subj, String pred, String obj) {
        return new Edge.Builder().sub(subj).pred(pred).obj(obj).build();
    }

    private ExistentialRestrictionExpression getRestriction(
            OWLClassExpression x) {
        if (x instanceof OWLObjectSomeValuesFrom) {
            OWLObjectSomeValuesFrom r = (OWLObjectSomeValuesFrom)x;
            OWLPropertyExpression p = r.getProperty();
            OWLClassExpression f = r.getFiller();
            if (p instanceof OWLObjectProperty && !f.isAnonymous()) {

                return new ExistentialRestrictionExpression.Builder()
                .propertyId(getPropertyId((OWLObjectProperty) p))
                .fillerId(getClassId((OWLClass) f))
                .build();
            }
        }
        return null;
    }

    //    private String shortenIRI(IRI iri) {
    //        prefixHelper
    //    }

    private String getPropertyId(OWLObjectProperty p) {
        return p.getIRI().toString();
    }
    private String getClassId(OWLClass c) {
        return c.getIRI().toString();
    }

    private String getNodeId(IRI s) {
        return s.toString();
    }

    public boolean isDefinitionProperty(IRI iri) {
        return iri.toString().equals("http://purl.obolibrary.org/obo/IAO_0000115");
    }

    public boolean isHasXrefProperty(IRI iri) {
        return iri.toString().equals("http://www.geneontology.org/formats/oboInOwl#hasDbXref");
    }
}
