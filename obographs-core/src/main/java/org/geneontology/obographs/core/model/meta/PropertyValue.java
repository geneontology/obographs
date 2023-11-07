package org.geneontology.obographs.core.model.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geneontology.obographs.core.model.Meta;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

/**
 * Associates the container object with a value via a property.
 * 
 * For example, a node representing an OWL class may contain a {@link Meta} object
 * containing a PropertyValue mapping to a textual definition string via a definition property.
 * 
 * Broadly, there are two categories of implementing class:
 * 
 *  1. PropertyValues corresponding to a specific explicitly modeled property type (e.g synonym)
 *  2. generic {@link BasicPropertyValue}s - anything property not explicitly modeled
 *  
 * A PropertyValue is minimally a tuple `(pred,value)`. However, each sub tuple may also
 * be "annotated" with additional metadata (this corresponds to an Axiom Annotation in OWL)
 * 
 *  - Any tuple can be supported by an array of xrefs.
 *  - Some implementing classes may choose to model additional explicit annotations (e.g. {@link SynonymPropertyValue})
 * 
 * @author cjm
 *
 */
@JsonPropertyOrder({"pred", "val", "xrefs", "meta"})
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface PropertyValue extends Comparable<PropertyValue> {

    static final Comparator<PropertyValue> COMPARATOR =
       Comparator.comparing(PropertyValue::getPred)
            .thenComparing(PropertyValue::getVal);

    /**
     * Predicates correspond to OWL properties. Like all preds in this datamodel,
     * a pred is represented as a String which denotes a CURIE
     * 
     * @return the pred
     */
    @JsonProperty
    @Value.Default
    default String getPred() {
        return "";
    }

    /**
     * The value of the property-value
     *
     * @return the val
     */
    @JsonProperty
    @Value.Default
    default String getVal() {
        return "";
    }

    /**
     * An array denoting objects that support the property value assertion
     *
     * @return the xrefs
     */
    @JsonProperty
    public List<String> getXrefs();

    @JsonProperty
    @Nullable
    public Meta getMeta();

    @Override
    default int compareTo(PropertyValue o) {
        return COMPARATOR.compare(this, o);
    }
}
