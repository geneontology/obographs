package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.geneontology.obographs.model.Meta;

import javax.annotation.Nullable;

public interface Axiom {

    /**
     * @return the meta
     */
    @JsonProperty
    @Nullable
    public Meta getMeta();
}
