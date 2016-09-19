package org.geneontology.obographs.owlapi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.io.OgJsonGenerator;
import org.geneontology.obographs.io.OgYamlGenerator;
import org.geneontology.obographs.model.GraphDocument;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class FromOwlPerfTest {

    @Test
    public void test() throws OWLOntologyCreationException, IOException {

        //String[] exts = {"obo","owl"};
        //File dir = new File("src/test/resources");
        //Collection<File> files = FileUtils.listFiles(dir, exts, true);
        String[] iris = {
                //"file:///Users/cjm/repos/uberon/uberon.owl",
                //http://purl.obolibrary.org/obo/uberon.owl"
                "http://purl.obolibrary.org/obo/ro.owl"
                //"http://purl.obolibrary.org/obo/mp.owl"      
        };

        for (String iriString : iris) {
            IRI iri = IRI.create(iriString);
            System.out.println("Converting: "+iri);
            //System.out.println(file.toPath());
            //System.out.println(file.toString());

            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = m.loadOntology(iri);

            FromOwl fromOwl = new FromOwl();
            GraphDocument gd = fromOwl.generateGraphDocument(ontology);

            String fn =  "foo.obo";
            String jsonStr = OgJsonGenerator.render(gd);
            export(jsonStr, fn, ".json");
            String yamlStr = OgYamlGenerator.render(gd);
            export(yamlStr, fn, ".yaml");
            //String ofn = fn.toString().replace(".obo", ".json").replace(".owl", ".json");
            //FileUtils.writeStringToFile(new File("examples/"+ofn), s);

        }
    }

    private void export(String s, Path fn, String suffix) throws IOException {
        String ofn = fn.toString().replace(".obo", suffix).replace(".owl", suffix);
        FileUtils.writeStringToFile(new File("examples/"+ofn), s);

    }
    private void export(String s, String fn, String suffix) throws IOException {
        String ofn = fn.toString().replace(".obo", suffix).replace(".owl", suffix);
        FileUtils.writeStringToFile(new File("examples/"+ofn), s);

    }

}
