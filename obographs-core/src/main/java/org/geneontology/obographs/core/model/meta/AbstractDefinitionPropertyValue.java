package org.geneontology.obographs.core.model.meta;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.immutables.value.Value;


/**
 * A {@link PropertyValue} that represents a textual definition of an ontology class or
 * property
 *
 * @author cjm
 */
@JsonPropertyOrder({"pred", "val", "xrefs", "meta"})
@Value.Immutable
public abstract class AbstractDefinitionPropertyValue implements PropertyValue {

}
