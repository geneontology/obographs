package org.geneontology.obographs.core.model.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;


@JsonPropertyOrder({"lbl", "pred", "val", "xrefs", "meta"})
@Value.Immutable
public abstract class AbstractXrefPropertyValue implements PropertyValue {

    /**
     * @return the lbl
     */
    @JsonProperty
    @Value.Default
    public String getLbl() {
        return "";
    }

}
