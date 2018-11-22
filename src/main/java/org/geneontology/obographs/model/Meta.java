package org.geneontology.obographs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.geneontology.obographs.model.meta.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A holder for metadata
 * 
 * The information in a Meta object consists sets of {@link PropertyValue} objects,
 * which associate the Meta object holder with some value via some property.
 * 
 * The set of PropertyValue objects can be partitioned into two subsets:
 * 
 *  1. PropertyValues corresponding to a specific explicitly modeled property type (e.g synonym)
 *  2. generic {@link BasicPropertyValue}s - anything property not explicitly modeled
 *  
 * 
 * 
 * @author cjm
 *
 */
public class Meta {

    private Meta(Builder builder) {
        definition = builder.definition;
        comments = builder.comments;
        subsets = builder.subsets;
        synonyms = builder.synonyms;
        xrefs = builder.xrefs;
        basicPropertyValues = builder.basicPropertyValues;
        version = builder.version;
        deprecated = builder.deprecated;
    }

    @JsonProperty private final DefinitionPropertyValue definition;
    @JsonProperty private final List<String> comments;
    @JsonProperty private final List<String> subsets;
    @JsonProperty private final List<XrefPropertyValue> xrefs;
    @JsonProperty private final List<SynonymPropertyValue> synonyms;
    @JsonProperty private final List<BasicPropertyValue> basicPropertyValues;
    @JsonProperty private final String version;
    @JsonProperty private final Boolean deprecated;

    /**
     * @return the definition
     */
    public DefinitionPropertyValue getDefinition() {
        return definition;
    }

    /**
     * @return the comments
     */
    public List<String> getComments() {
        return comments;
    }

    /**
     * @return the xrefs
     */
    public List<XrefPropertyValue> getXrefs() {
        return xrefs;
    }

    @JsonIgnore
    public List<String> getXrefsValues() {
        return xrefs.stream().map(AbstractPropertyValue::getVal).collect(Collectors.toList());
    }

    /**
     * @return the subsets
     */
    public List<String> getSubsets() {
        return subsets;
    }

    /**
     * @return the synonymPropertyValues
     */
    public List<SynonymPropertyValue> getSynonyms() {
        return synonyms;
    }

    /**
     * @return the basicPropertyValues
     */
    public List<BasicPropertyValue> getBasicPropertyValues() {
        return basicPropertyValues;
    }

    /**
     * this is typically only set for meta objects at the level of a graph/ontology
     * 
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public static class Builder {

        @JsonProperty
        public List<String> subsets;
        @JsonProperty
        public List<BasicPropertyValue> basicPropertyValues;
        @JsonProperty
        public List<SynonymPropertyValue> synonyms;
        @JsonProperty
        public List<String> comments;
        @JsonProperty
        public DefinitionPropertyValue definition;
        @JsonProperty
        public List<XrefPropertyValue> xrefs;
        @JsonProperty
        public String version;
        @JsonProperty
        public Boolean deprecated;
        

        public Builder definition(DefinitionPropertyValue definition) {
            this.definition = definition;
            return this;
        }
        public Builder definition(String defval) {
            this.definition = new DefinitionPropertyValue.Builder().val(defval).build();
            //((org.geneontology.obographs.model.meta.DefinitionPropertyValue.Builder) new DefinitionPropertyValue.Builder().val(defval)).build();
            return this;
        }
        public Builder subsets(List<String> subsets) {
            this.subsets = subsets;
            return this;
        }
        public Builder subsets(String[] subsets) {
            this.subsets = Arrays.asList(subsets);
            return this;
        }
        public Builder addSubset(String subset) {
            if (this.subsets == null)
                this.subsets(new ArrayList<String>());
            this.subsets.add(subset);
            return this;
        }

        public Builder comments(List<String> comments) {
            this.comments = comments;
            return this;
        }
        public Builder basicPropertyValues(List<BasicPropertyValue> basicPropertyValues) {
            this.basicPropertyValues = basicPropertyValues;
            return this;
        }
        public Builder addBasicPropertyValue(BasicPropertyValue pv) {
            if (this.basicPropertyValues == null)
                this.basicPropertyValues = new ArrayList<>();
            this.basicPropertyValues.add(pv);
            return this;         
        }
 
        public Builder synonyms(List<SynonymPropertyValue> synonyms) {
            this.synonyms = synonyms;
            return this;
        }
        public Builder addSynonym(SynonymPropertyValue syn) {
            if (this.synonyms == null)
                this.synonyms = new ArrayList<>();
            this.synonyms.add(syn);
            return this;         
        }
        public Builder addXref(XrefPropertyValue xref) {
            if (this.xrefs == null)
                this.xrefs = new ArrayList<>();
            this.xrefs.add(xref);
            return this; 
        }
        public Builder addComment(String comment) {
            if (this.comments == null)
                this.comments = new ArrayList<>();
            this.comments.add(comment);
            return this; 
        }

        public Builder xrefs(List<XrefPropertyValue> xrefs) {
            this.xrefs = xrefs;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder deprecated(Boolean deprecated) {
            this.deprecated = deprecated;
            return this;
        }


        public Meta build() {
            return new Meta(this);
        }
  
    }

}
