package org.geneontology.obographs.owlapi;

import org.geneontology.obographs.core.model.meta.AbstractSynonymPropertyValue.Scope;

import java.util.Map;

public class SynonymVocabulary {

    public static final String SYNONYM_TYPE = "http://www.geneontology.org/formats/oboInOwl#hasSynonymType";

    private static final Map<String, Scope> iriToScopeMap = Map.of(
            "http://www.geneontology.org/formats/oboInOwl#hasExactSynonym", Scope.EXACT,
            "http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym", Scope.RELATED,
            "http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym", Scope.NARROW,
            "http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym", Scope.BROAD
    );

    private SynonymVocabulary() {
        // utility class
    }

    public static Scope getScope(String iri) {
        return iriToScopeMap.get(iri);
    }

    public static boolean containsIri(String iri) {
        return iriToScopeMap.containsKey(iri);
    }

}
