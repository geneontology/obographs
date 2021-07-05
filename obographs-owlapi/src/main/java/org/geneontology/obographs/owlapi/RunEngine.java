package org.geneontology.obographs.owlapi;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.geneontology.obographs.core.io.OgJsonGenerator;
import org.geneontology.obographs.core.io.OgYamlGenerator;
import org.geneontology.obographs.core.model.GraphDocument;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class RunEngine {


    @Parameter(names = {"-v", "--verbose"}, description = "Level of verbosity")
    private Integer verbose = 1;

    @Parameter(names = {"-o", "--out"}, description = "output json/yaml file")
    private String outpath;

    @Parameter(names = {"-t", "--to"}, description = "output format: json or yaml")
    private String outformat;


    @Parameter(description = "Files")
    private List<String> files = new ArrayList<>();

    OWLOntologyManager manager;


    public static void main(String... args) throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
        RunEngine main = new RunEngine();
        new JCommander(main, args);
        main.run();
    }

    public void run() throws OWLOntologyCreationException, IOException {

        FromOwl fromOwl = new FromOwl();
        File file = new File(files.get(0));
        OWLOntology ont = this.loadOWL(file);
        GraphDocument gd = fromOwl.generateGraphDocument(ont);

        try(Writer writer = Files.newBufferedWriter(Paths.get(outpath), StandardCharsets.UTF_8)) {
            if (outformat == null || outformat.equals("json")) {
                OgJsonGenerator.write(writer, gd);
            } else if (outformat.equals("yaml")) {
                OgYamlGenerator.write(writer, gd);
            } else {
                throw new IOException("no such format " + outformat);
            }
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

