package org.geneontology.obographs.core.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;

import java.util.Comparator;

/**
 * Corresponds to an axiom of the form C = X1 and ... and Xn,
 * Where X_i is either a named class or OWL Restriction
 * 
 * 
 * @author cjm
 *
 */
@JsonPropertyOrder({"propertyId", "fillerId", "meta"})
@Value.Immutable
public abstract class AbstractExistentialRestrictionExpression implements Expression, Comparable<AbstractExistentialRestrictionExpression> {

    private static final Comparator<AbstractExistentialRestrictionExpression> COMPARATOR =
            Comparator.comparing(AbstractExistentialRestrictionExpression::getPropertyId)
                    .thenComparing(AbstractExistentialRestrictionExpression::getFillerId);
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

    @Override
    public int compareTo(AbstractExistentialRestrictionExpression o) {
        return COMPARATOR.compare(this, o);
    }
}
