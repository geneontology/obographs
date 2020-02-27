package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;


/**
 * A {@link PropertyValue} that represents a textual definition of an ontology class or
 * property
 *
 * @author cjm
 */
@JsonSerialize(as = DefinitionPropertyValue.class)
@JsonDeserialize(as = DefinitionPropertyValue.class)
@Value.Immutable
public abstract class AbstractDefinitionPropertyValue implements PropertyValue {

}
