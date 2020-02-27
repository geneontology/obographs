package org.geneontology.obographs.model.meta;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * A generic {@link PropertyValue} that is not explicitly modeled
 *
 * @author cjm
 */
@JsonSerialize(as = BasicPropertyValue.class)
@JsonDeserialize(as = BasicPropertyValue.class)
@Value.Immutable
public abstract class AbstractBasicPropertyValue implements PropertyValue {

}
