package org.geneontology.obographs.runner;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.io.OgJsonGenerator;
import org.geneontology.obographs.io.OgYamlGenerator;
import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.owlapi.FromOwl;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class RunEngine {


    @Parameter(names = { "-v",  "--verbose" }, description = "Level of verbosity")
    private Integer verbose = 1;

    @Parameter(names = { "-o", "--out"}, description = "output json/yaml file")
    private String outpath;

    @Parameter(names = { "-t", "--to"}, description = "output format: json or yaml")
    private String outformat;



    @Parameter(description = "Files")
    private List<String> files = new ArrayList<>();

    OWLOntologyManager manager;


    public static void main(String ... args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        RunEngine main = new RunEngine();
        new JCommander(main, args);
        main.run();
    }

    public void run() throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {

        FromOwl fromOwl = new FromOwl();
        File file = new File(files.get(0));
        OWLOntology ont   = this.loadOWL(file);
        GraphDocument gd = fromOwl.generateGraphDocument(ont);

        String doc;
        if (outformat == null || outformat.equals("json")) {
            doc = OgJsonGenerator.render(gd);
        }
        else if(outformat.equals("yaml")) {
            doc = OgYamlGenerator.render(gd);
        }
        else {
            throw new IOException("no such format "+outformat);
        }

        if (outpath == null) {
            System.out.println(doc);            
        }
        else {
            FileUtils.writeStringToFile(new File(outpath), doc);
        }

    }

    private OWLOntologyManager getOWLOntologyManager() {
        if (manager == null)
            manager = OWLManager.createOWLOntologyManager();
        return manager;
    }
    /**
     * @param iri
     * @return OWL Ontology 
     * @throws OWLOntologyCreationException
     */
    public OWLOntology loadOWL(IRI iri) throws OWLOntologyCreationException {
        return getOWLOntologyManager().loadOntology(iri);
    }

    /**
     * @param file
     * @return OWL Ontology
     * @throws OWLOntologyCreationException
     */
    public OWLOntology loadOWL(File file) throws OWLOntologyCreationException {
        IRI iri = IRI.create(file);
        return getOWLOntologyManager().loadOntologyFromOntologyDocument(iri);       
    }
}

