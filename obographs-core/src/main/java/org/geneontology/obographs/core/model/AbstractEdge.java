package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ComparisonChain;
import org.immutables.value.Value;

/**
 * An edge connects two nodes via a predicate
 * 
 * @author cjm
 *
 */
@JsonPropertyOrder({"sub", "pred", "obj", "meta"})
@Value.Immutable
public abstract class AbstractEdge implements NodeOrEdge, Comparable<AbstractEdge> {

	@JsonProperty
	public abstract String getSub();
	@JsonProperty
	public abstract String getPred();
	@JsonProperty
	public abstract String getObj();

	public int compareTo(AbstractEdge other) {
		return ComparisonChain.start()
				.compare(this.getSub(), other.getSub())
				.compare(this.getPred(), other.getPred())
				.compare(this.getObj(), other.getObj())
				.result();
	}
}
