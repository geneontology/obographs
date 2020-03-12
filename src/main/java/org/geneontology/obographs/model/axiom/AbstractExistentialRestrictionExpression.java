package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * Corresponds to an axiom of the form C = X1 and ... and Xn,
 * Where X_i is either a named class or OWL Restriction
 * 
 * 
 * @author cjm
 *
 */
@JsonSerialize(as = ExistentialRestrictionExpression.class)
@JsonDeserialize(as = ExistentialRestrictionExpression.class)
@JsonPropertyOrder({"propertyId", "fillerId", "meta"})
@Value.Immutable
public abstract class AbstractExistentialRestrictionExpression implements Expression {

    /**
     * @return the propertyId
     */
    @JsonProperty
    public abstract String getPropertyId();

    /**
     * @return the representativeNodeId
     */
    @JsonProperty
    public abstract String getFillerId();

}
