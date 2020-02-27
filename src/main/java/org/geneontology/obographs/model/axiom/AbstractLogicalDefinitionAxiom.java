package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

/**
 * Corresponds to an axiom of the form C = X1 and ... and Xn,
 * Where X_i is either a named class or OWL Restriction
 * 
 * 
 * @author cjm
 *
 */
@JsonSerialize(as = LogicalDefinitionAxiom.class)
@JsonDeserialize(as = LogicalDefinitionAxiom.class)
@Value.Immutable
public abstract class AbstractLogicalDefinitionAxiom implements Axiom {

    /**
     * @return the representativeNodeId
     */
    @JsonProperty
    public abstract String getDefinedClassId();
    /**
     * @return the nodeIds
     */
    @JsonProperty
    public abstract List<String> getGenusIds();

    /**
     * @return the restrictions
     */
    @JsonProperty
    public abstract List<ExistentialRestrictionExpression> getRestrictions();

}
