package org.geneontology.obographs.core.model.meta;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;

/**
 * A generic {@link PropertyValue} that is not explicitly modeled
 *
 * @author cjm
 */
@JsonPropertyOrder({"pred", "val", "xrefs", "meta"})
@Value.Immutable
public abstract class AbstractBasicPropertyValue implements PropertyValue {

}
