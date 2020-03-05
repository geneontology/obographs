package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.geneontology.obographs.model.Meta;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface Axiom {
    /**
     * @return the meta
     */
    @JsonProperty
    public Meta getMeta();
}
