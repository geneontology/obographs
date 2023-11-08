package org.geneontology.obographs.owlapi;

import org.geneontology.obographs.core.model.meta.AbstractSynonymPropertyValue.SCOPES;

import java.util.HashMap;
import java.util.Map;

public class SynonymVocabulary {

    public static final String SYNONYM_TYPE = "http://www.geneontology.org/formats/oboInOwl#hasSynonymType";

    Map<String,SCOPES> iriToScopeMap = new HashMap<>();

    
    public SynonymVocabulary() {
        super();
        setDefaults();
    }

    public void setDefaults() {
        set("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym", SCOPES.EXACT);
        set("http://www.geneontology.org/formats/oboInOwl#hasRelatedSynonym", SCOPES.RELATED);
        set("http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym", SCOPES.NARROW);
        set("http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym", SCOPES.BROAD);
    }

    /**
     * @return the iriToScopeMap
     */
    public Map<String, SCOPES> getIriToScopeMap() {
        return iriToScopeMap;
    }
    
    public void set(String iri, SCOPES scope) {
        iriToScopeMap.put(iri, scope);
    }
    public SCOPES get(String iri) {
        return iriToScopeMap.get(iri);
    }
    public boolean contains(String iri) {
        return iriToScopeMap.containsKey(iri);
    }

}
