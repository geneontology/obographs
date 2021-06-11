package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface NodeOrEdge {

    static final Meta EMPTY_META = new Meta.Builder().build();

    /**
     * @return the meta
     */
    @JsonProperty
    @Nullable
    public Meta getMeta();
}
