package org.geneontology.obographs.model.axiom;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.geneontology.obographs.model.Edge;
import org.immutables.value.Value;

import java.util.Set;

/**
 * This combined ObjectPropertyDomain, ObjectPropertyRange, and some AllValuesFrom expressions into a single convenience structure
 *
 * @author cjm
 */
//@JsonDeserialize(builder = DomainRangeAxiom.Builder.class)
@JsonSerialize(as = DomainRangeAxiom.class)
@JsonDeserialize(as = DomainRangeAxiom.class)
@Value.Immutable
public abstract class AbstractDomainRangeAxiom implements Axiom {

//    @JsonProperty
//    public abstract Meta getMeta();
//
//    private DomainRangeAxiom(Builder builder) {
//        super(builder);
//        predicateId = builder.predicateId;
//        domainClassIds = builder.domainClassIds;
//        rangeClassIds = builder.rangeClassIds;
//        allValuesFromEdges = builder.allValuesFromEdges;
//    }
//
//    private final String predicateId;
//    private final Set<String> domainClassIds;
//    private final Set<String> rangeClassIds;
//    private final Set<Edge> allValuesFromEdges;

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
    public abstract Set<String> getDomainClassIds();

    /**
     * For multiple ranges, this is treated as intersection
     *
     * @return the rangeClassIds
     */
    @JsonProperty
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
    public abstract Set<Edge> getAllValuesFromEdges();

//    public static class Builder extends AbstractAxiom.Builder {
//
//        @JsonProperty
//        private String predicateId;
//        @JsonProperty
//        private Set<String> domainClassIds;
//        @JsonProperty
//        private Set<String> rangeClassIds;
//        @JsonProperty
//        private Meta meta;
//        @JsonProperty
//        private Set<Edge> allValuesFromEdges;
//
//        public Builder predicateId(String predicateId) {
//            this.predicateId = predicateId;
//            return this;
//        }
//        public String predicateId() {
//            return predicateId;
//        }
//
//        public Builder domainClassId(Set<String> domainClassId) {
//            this.domainClassIds = domainClassIds;
//            return this;
//        }
//        public Builder domainClassId(String domainClassId) {
//            this.domainClassIds = Collections.singleton(domainClassId);
//            return this;
//        }
//        public Builder addDomainClassId(String domainClassId) {
//            if (domainClassIds == null)
//                domainClassIds = new HashSet<>();
//            this.domainClassIds.add(domainClassId);
//            return this;
//        }
//
//        public Builder rangeClassIds(Set<String> rangeClassIds) {
//            this.rangeClassIds = rangeClassIds;
//            return this;
//        }
//        public Builder rangeClassId(String rangeClassId) {
//            this.rangeClassIds = Collections.singleton(rangeClassId);
//            return this;
//        }
//        public Builder addRangeClassId(String rangeClassId) {
//            if (rangeClassIds == null)
//                rangeClassIds = new HashSet<>();
//            this.rangeClassIds.add(rangeClassId);
//            return this;
//        }
//
//        public Builder addAllValuesFrom(Edge edge) {
//            if (allValuesFromEdges == null)
//                allValuesFromEdges = new HashSet<>();
//            this.allValuesFromEdges.add(edge);
//            return this;
//        }
//
//        @JsonCreator
//        public DomainRangeAxiom build() {
//            return new DomainRangeAxiom(this);
//        }
//
//    }


}
