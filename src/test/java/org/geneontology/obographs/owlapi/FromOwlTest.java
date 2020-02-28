package org.geneontology.obographs.owlapi;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.io.OgJsonGenerator;
import org.geneontology.obographs.io.OgJsonReader;
import org.geneontology.obographs.io.OgYamlGenerator;
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
 * TODO - this test generates json from OWL but does not actually test the content returned
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
            //System.out.println(file.toPath());
            //System.out.println(file.toString());

            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = m.loadOntologyFromOntologyDocument(file);

            FromOwl fromOwl = new FromOwl();
            GraphDocument gd = fromOwl.generateGraphDocument(ontology);

            Path fn = file.toPath().getFileName();
            String jsonStr = OgJsonGenerator.render(gd);
            File jsonOutFile = export(jsonStr, fn, ".json");

            String yamlStr = OgYamlGenerator.render(gd);
            export(yamlStr, fn, ".yaml");
            //String ofn = fn.toString().replace(".obo", ".json").replace(".owl", ".json");
            //FileUtils.writeStringToFile(new File("examples/"+ofn), s);
            // read it back in from JSON - this is where you might find an error
            GraphDocument graphDocument = OgJsonReader.readFile(jsonOutFile);

            // ideally, but not tried this yet!
            assertThat(gd, equalTo(graphDocument));
        }
    }

    private File export(String s, Path fn, String suffix) throws IOException {
        String ofn = fn.toString().replace(".obo", suffix).replace(".owl", suffix);
        File outFile = new File("examples/" + ofn);
        FileUtils.writeStringToFile(outFile, s);
        return outFile;
    }

}
