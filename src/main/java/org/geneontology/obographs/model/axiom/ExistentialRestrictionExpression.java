package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Corresponds to an axiom of the form C = X1 and ... and Xn,
 * Where X_i is either a named class or OWL Restriction
 * 
 * 
 * @author cjm
 *
 */
@JsonDeserialize(builder = ExistentialRestrictionExpression.Builder.class)
public class ExistentialRestrictionExpression extends AbstractExpression {

    private ExistentialRestrictionExpression(Builder builder) {
        super(builder);
        fillerId = builder.fillerId;
        propertyId = builder.propertyId;
    }

    private final String propertyId;
    private final String fillerId;



    /**
     * @return the representativeNodeId
     */
    public String getFillerId() {
        return fillerId;
    }




    /**
     * @return the propertyId
     */
    public String getPropertyId() {
        return propertyId;
    }




    public static class Builder extends AbstractExpression.Builder {

        @JsonProperty
        private String propertyId;
        @JsonProperty
        private String fillerId;
 
        public Builder propertyId(String propertyId) {
            this.propertyId = propertyId;
            return this;
        }

        public Builder fillerId(String fillerId) {
            this.fillerId = fillerId;
            return this;
        }

        @JsonCreator
        public ExistentialRestrictionExpression build() {
            return new ExistentialRestrictionExpression(this);
        }

    }


}
