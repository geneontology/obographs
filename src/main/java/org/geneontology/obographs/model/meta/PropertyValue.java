package org.geneontology.obographs.model.meta;

import java.util.List;

import org.geneontology.obographs.model.Meta;

public interface PropertyValue {

    /**
     * @return the pred
     */
    public String getPred();

    /**
     * @return the xrefs
     */
    public List<String> getXrefs();

    /**
     * @return the val
     */
    public String getVal();



    /**
     * @return the meta
     */
    public Meta getMeta();

}
