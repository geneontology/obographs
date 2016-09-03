package org.geneontology.obographs.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.geneontology.obographs.model.meta.BasicPropertyValue;
import org.geneontology.obographs.model.meta.DefinitionPropertyValue;
import org.geneontology.obographs.model.meta.SynonymPropertyValue;
import org.geneontology.obographs.model.meta.XrefPropertyValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Meta {

    private Meta(Builder builder) {
        definition = builder.definition;
        comments = builder.comments;
        subsets = builder.subsets;
        synonyms = builder.synonyms;
        xrefs = builder.xrefs;
        basicPropertyValues = builder.basicPropertyValues;
    }

    @JsonProperty private final DefinitionPropertyValue definition;
    @JsonProperty private final List<String> comments;
    @JsonProperty private final List<String> subsets;
    @JsonProperty private final List<XrefPropertyValue> xrefs;
    @JsonProperty private final List<SynonymPropertyValue> synonyms;
    @JsonProperty private final List<BasicPropertyValue> basicPropertyValues;


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
        return xrefs.stream().map( x -> x.getVal()).collect(Collectors.toList());
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

        public Builder comments(List<String> comments) {
            this.comments = comments;
            return this;
        }
        public Builder basicPropertyValues(List<BasicPropertyValue> basicPropertyValues) {
            this.basicPropertyValues = basicPropertyValues;
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

        public Builder xrefs(List<XrefPropertyValue> xrefs) {
            this.xrefs = xrefs;
            return this;
        }



        public Meta build() {
            return new Meta(this);
        }
  
    }

}
