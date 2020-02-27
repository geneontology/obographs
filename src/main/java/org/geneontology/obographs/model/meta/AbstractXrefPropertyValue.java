package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;


@JsonSerialize(as = XrefPropertyValue.class)
@JsonDeserialize(as = XrefPropertyValue.class)
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
