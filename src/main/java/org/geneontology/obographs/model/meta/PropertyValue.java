package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.geneontology.obographs.model.Meta;

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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface PropertyValue {

    /**
     * @return the meta
     */
    @JsonProperty
    public Meta getMeta();

    /**
     * Predicates correspond to OWL properties. Like all preds in this datamodel,
     * a pred is represented as a String which denotes a CURIE
     *
     * @return the pred
     */
    @JsonProperty
    public String getPred();

    /**
     * The value of the property-value
     *
     * @return the val
     */
    @JsonProperty
    public String getVal();

    /**
     * An array denoting objects that support the property value assertion
     *
     * @return the xrefs
     */
    @JsonProperty
    public List<String> getXrefs();

}
