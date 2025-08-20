package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.geneontology.obographs.core.model.meta.*;
import org.immutables.value.Value;

import javax.annotation.Nullable;
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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonPropertyOrder({"definition", "comments", "subsets", "synonyms", "xrefs", "basicPropertyValues", "version", "deprecated"})
@Value.Immutable
public abstract class AbstractMeta {

    @JsonProperty
    @Nullable
    public abstract DefinitionPropertyValue definition();

    @JsonProperty
    public abstract List<String> comments();

    @JsonProperty
    public abstract List<String> subsets();

    @JsonProperty
    public abstract List<SynonymPropertyValue> synonyms();

    @JsonProperty
    public abstract List<XrefPropertyValue> xrefs();

    @JsonIgnore
    @Value.Default
    public List<String> xrefsValues() {
        return xrefs().stream().map(XrefPropertyValue::val).toList();
    }

    @JsonProperty
    public abstract List<BasicPropertyValue> basicPropertyValues();

    @JsonProperty
    @Value.Default
    public String version(){
        return "";
    }

    @JsonProperty
    @Value.Default
    public boolean isDeprecated() {
        return false;
    }

}
