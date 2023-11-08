package org.geneontology.obographs.core.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */

@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS) // Make it class retention for incremental compilation
@Value.Style(
        get = {"is*", "get*"}, // Detect 'get' and 'is' prefixes in accessor methods
        depluralize = true, // enable feature
        typeAbstract = {"Abstract*"}, // 'Abstract' prefix will be detected and trimmed
        typeImmutable = "*", // No prefix or suffix for generated immutable type
        builder = "new", // construct builder using 'new' instead of factory method
        visibility = Value.Style.ImplementationVisibility.SAME // Generated class will be always public
        // Do NOT enable jdk9Collections = true as this will use the inherently unordered Set.of which will destroy the
        // reproducible output of the library when writing to JSON/YAML
)
@JsonSerialize
public @interface OboGraph {
}
