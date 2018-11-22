package org.geneontology.obographs.io;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.formats.PrefixDocumentFormatImpl;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

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
