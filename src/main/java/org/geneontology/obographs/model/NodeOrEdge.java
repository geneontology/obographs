package org.geneontology.obographs.model;

import org.immutables.value.Value;

public interface NodeOrEdge {

    static final Meta EMPTY_META = new Meta.Builder().build();

    /**
     * @return the meta
     */
    @Value.Default
//    @Nullable
    default Meta getMeta(){
        return EMPTY_META;
    }
}
