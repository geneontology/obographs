package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

/**
 * An edge connects two nodes via a predicate
 * 
 * @author cjm
 *
 */
@JsonSerialize(as = Edge.class)
@JsonDeserialize(as = Edge.class)
@Value.Immutable
public abstract class AbstractEdge implements NodeOrEdge {

	@JsonProperty
	public abstract String getSub();
	@JsonProperty
	public abstract String getPred();
	@JsonProperty
	public abstract  String getObj();

}
