package org.geneontology.obographs.owlapi;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.core.io.OgJsonGenerator;
import org.geneontology.obographs.core.io.OgYamlGenerator;
import org.geneontology.obographs.core.model.GraphDocument;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FromOwlPerfTest {

    @Test
    public void test() throws OWLOntologyCreationException, IOException {

        //String[] exts = {"obo","owl"};
        //File dir = new File("src/test/resources");
        //Collection<File> files = FileUtils.listFiles(dir, exts, true);
        List<String> iris = List.of(
                //"file:///Users/cjm/repos/uberon/uberon.owl",
                //http://purl.obolibrary.org/obo/uberon.owl"
                "http://purl.obolibrary.org/obo/ro.owl"
                //"http://purl.obolibrary.org/obo/mp.owl"      
        );

        for (String iriString : iris) {
            IRI iri = IRI.create(iriString);
            System.out.println("Converting: " + iri);
            //System.out.println(file.toPath());
            //System.out.println(file.toString());

            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            System.out.println("Loading OWL");
            OWLOntology ontology = m.loadOntology(iri);

            String ontologyId = iri.getShortForm().replace(".obo", "").replace(".owl", "");
            System.out.println("Converting " + ontologyId + " from OWL:");
            FromOwl fromOwl = new FromOwl();
            GraphDocument gd = fromOwl.generateGraphDocument(ontology);

            System.out.println("Writing JSON/YAML files...");
            OgJsonGenerator.write(Files.createTempFile(ontologyId, ".json").toFile(), gd);
            OgYamlGenerator.write(Files.createTempFile(ontologyId, ".yaml").toFile(), gd);
            System.out.println("Done");
        }
    }

    private void export(String s, Path fn, String suffix) throws IOException {
        String ofn = fn.toString().replace(".obo", suffix).replace(".owl", suffix);
        FileUtils.writeStringToFile(new File("examples/"+ofn), s);
    }
}
