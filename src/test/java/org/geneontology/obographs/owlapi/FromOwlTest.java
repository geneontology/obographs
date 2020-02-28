package org.geneontology.obographs.owlapi;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.io.OgJsonGenerator;
import org.geneontology.obographs.io.OgJsonReader;
import org.geneontology.obographs.io.OgYamlGenerator;
import org.geneontology.obographs.io.OgYamlReader;
import org.geneontology.obographs.model.GraphDocument;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Generates json from OWL and tests the content returned for equality.
 *
 * @author cjm
 *
 */
public class FromOwlTest {

    @Test
    public void test() throws OWLOntologyCreationException, IOException {

        String[] exts = {"obo","owl"};
        File dir = new File("src/test/resources");
        Collection<File> files = FileUtils.listFiles(dir, exts, true);

        for (File file : files) {
            System.out.println("Converting: "+file);

            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = m.loadOntologyFromOntologyDocument(file);

            FromOwl fromOwl = new FromOwl();
            GraphDocument gd = fromOwl.generateGraphDocument(ontology);

            Path fn = file.toPath().getFileName();
            String jsonStr = OgJsonGenerator.render(gd);
            File jsonOutFile = createFileWithSuffix(fn, ".json");
            FileUtils.writeStringToFile(jsonOutFile, jsonStr);

            String yamlStr = OgYamlGenerator.render(gd);
            File yamlOutFile = createFileWithSuffix(fn, ".yaml");
            FileUtils.writeStringToFile(yamlOutFile, yamlStr);

            // read it back in from JSON
            // cross fingers...
            assertThat(gd, equalTo(OgJsonReader.readFile(jsonOutFile)));
            assertThat(gd, equalTo(OgYamlReader.readFile(yamlOutFile)));
        }
    }

    private File createFileWithSuffix(Path fn, String suffix) {
        String ofn = fn.toString().replace(".obo", suffix).replace(".owl", suffix);
        return new File("examples/" + ofn);
    }

}
