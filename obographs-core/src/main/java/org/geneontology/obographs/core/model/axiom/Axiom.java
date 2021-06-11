package org.geneontology.obographs.core.model.axiom;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.geneontology.obographs.core.model.Meta;

import javax.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface Axiom {

    /**
     * @return the meta
     */
    @JsonProperty
    @Nullable
    public Meta getMeta();
}
