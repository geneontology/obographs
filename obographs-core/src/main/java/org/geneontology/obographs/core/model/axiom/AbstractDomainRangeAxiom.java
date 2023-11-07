package org.geneontology.obographs.core.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geneontology.obographs.core.model.Edge;
import org.immutables.value.Value;

import java.util.Comparator;
import java.util.Set;

/**
 * This combined ObjectPropertyDomain, ObjectPropertyRange, and some AllValuesFrom expressions into a single convenience structure
 *
 * @author cjm
 */
@JsonPropertyOrder({"predicateId", "domainClassIds", "rangeClassIds", "allValuesFromEdges", "meta"})
@Value.Immutable
public abstract class AbstractDomainRangeAxiom implements Axiom, Comparable<AbstractDomainRangeAxiom> {

    private static final Comparator<AbstractDomainRangeAxiom> COMPARATOR =
            Comparator.comparing(AbstractDomainRangeAxiom::getPredicateId);

    /**
     * @return the predicateId
     */
    @JsonProperty
    public abstract String getPredicateId();

    /**
     * For multiple domains, this is treated as intersection
     *
     * @return the domainClassIds
     */
    @JsonProperty
//    @Value.NaturalOrder
    public abstract Set<String> getDomainClassIds();

    /**
     * For multiple ranges, this is treated as intersection
     *
     * @return the rangeClassIds
     */
    @JsonProperty
//    @Value.NaturalOrder
    public abstract Set<String> getRangeClassIds();

    /**
     * Set of edges representing `X SubClassOf P only Y` axioms.
     * <p>
     * Note that these are not in the main graph.edges object, as the edge
     * graph is intended to be an existential graph. Most applications that do
     * not perform a reasoning function have no use for universal axioms.
     *
     * @return the allValuesFromEdges
     */
    @JsonProperty
//    @Value.NaturalOrder
    public abstract Set<Edge> getAllValuesFromEdges();

    @Override
    public int compareTo(AbstractDomainRangeAxiom o) {
        return COMPARATOR.compare(this, o);
    }
}
