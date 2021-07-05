package org.geneontology.obographs.owlapi;

import org.semanticweb.owlapi.formats.PrefixDocumentFormatImpl;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

import javax.annotation.Nonnull;

/**
 * @author cjm
 *
 */
public class OboGraphJsonDocumentFormat extends PrefixDocumentFormatImpl implements OWLDocumentFormat {

    @Nonnull
    @Override
    public String getKey() {
        return "OboGraphs JSON Syntax";
    }

}
