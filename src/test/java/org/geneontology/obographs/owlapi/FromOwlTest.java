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
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Generates json from OWL and tests the content returned for equality.
 *
 * @author cjm
 *
 */
public class FromOwlTest {

    private final Path examplesPath = Paths.get("examples");

    /**
     * This method was run to produce the files in the examples directory. It is not run as a test as the other tests in
     * this class rely on the output of this to verify their output. Should the model change then this method will need
     * to be run again otherwise the tests will (should!) fail.
     * @throws Exception
     */
    public void createExampleFiles() throws Exception {
        System.out.println("Creating example files in examples/ directory");
        for (File file : getTestOboOwlFiles()) {
            System.out.println("Loading " + file.getPath());
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = m.loadOntologyFromOntologyDocument(file);
            System.out.println("Converting to obographs...");
            GraphDocument gd = new FromOwl().generateGraphDocument(ontology);

            // write json
            String jsonFileName = replaceOboOwlExtensionWithSuffix(file, ".json");
            Path jsonFile = examplesPath.resolve(jsonFileName);
            System.out.println("Writing " + jsonFile);
            OgJsonGenerator.write(Files.newBufferedWriter(jsonFile, StandardCharsets.UTF_8), gd);

            // write yaml
            String yamlFileName = replaceOboOwlExtensionWithSuffix(file, ".yaml");
            Path yamlFile = examplesPath.resolve(yamlFileName);
            System.out.println("Writing " + yamlFile);
            OgYamlGenerator.write(Files.newBufferedWriter(yamlFile, StandardCharsets.UTF_8), gd);
        }
    }

    /**
     * Check the owl files can be converted to a GraphDocument and the JSON and YAML outputs are line-by-line identical
     * to the examples.
     *
     * @throws Exception
     */
    @Test
    public void testJsonAndYamlFilesAreReproducible() throws Exception {
        for (File file : getTestOboOwlFiles()) {
            System.out.println("Checking " + file);

            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = m.loadOntologyFromOntologyDocument(file);

            GraphDocument testGraphDocument = new FromOwl().generateGraphDocument(ontology);
            String strippedFileName = stripOboOwlExtensionFromFileName(file);

            List<String> formats = Arrays.asList(".json", ".yaml");
            for (String format : formats) {
                System.out.println("Checking " + strippedFileName + format);
                Path exampleFile = examplesPath.resolve(strippedFileName + format);
                // check file output
                Path newFile = writeNewTempFileWithFormat(testGraphDocument, strippedFileName, format);
                compareFilesLineByLine(exampleFile, newFile);
                // check object equality
                GraphDocument exampleGraphDocument = readGraphDocumentFrom(exampleFile, format);
                assertThat(testGraphDocument, equalTo(exampleGraphDocument));
            }
        }
    }

    private GraphDocument readGraphDocumentFrom(Path newFilePath, String format) throws IOException {
        switch (format) {
            case ".json":
                return OgJsonReader.readFile(newFilePath.toFile());
            case ".yaml":
                return OgYamlReader.readFile(newFilePath.toFile());
        }
        throw new IllegalArgumentException("Format '" + format + "' not recognised");
    }

    private Path writeNewTempFileWithFormat(GraphDocument gd, String strippedFileName, String format) throws IOException {
        // write new GraphDocument to temp file
        Path newJson = Files.createTempFile(strippedFileName, format);
        // compare new and example json files
        if (format.equals(".json")) {
            OgJsonGenerator.write(newJson.toFile(), gd);
        } else if (format.equals(".yaml")){
            OgYamlGenerator.write(newJson.toFile(), gd);
        } else {
            throw new IllegalArgumentException("Format '" + format + "' not recognised");
        }
        return newJson;
    }

    private Collection<File> getTestOboOwlFiles() {
        String[] exts = {"obo","owl"};
        File dir = new File("src/test/resources");
        return FileUtils.listFiles(dir, exts, true);
    }

    private void compareFilesLineByLine(Path originalJsonFile, Path jsonOutFile) throws IOException {
        BufferedReader br1 = Files.newBufferedReader(originalJsonFile);
        BufferedReader br2 = Files.newBufferedReader(jsonOutFile);
        String line1 = br1.readLine();
        String line2 = br2.readLine();
        int line = 1;
        while(line1 != null && line2 != null) {
            if  (!line1.equals(line2)){
                System.out.println("Line mismatch:");
                System.out.println("Exp: " + line1);
                System.out.println("Got: " + line2);
                fail("Mismatch in " + jsonOutFile + " line " + line);
            }
            line++;
            line1 = br1.readLine();
            line2 = br2.readLine();
        }
    }

    private String replaceOboOwlExtensionWithSuffix(File file, String suffix) {
        return stripOboOwlExtensionFromFileName(file) + suffix;
    }

    private String stripOboOwlExtensionFromFileName(File file) {
        return stripOboOwlExtensionFromFileName(file.toPath());
    }

    private String stripOboOwlExtensionFromFileName(Path path) {
        return path.getFileName().toString().replace(".obo", "").replace(".owl", "");
    }
}
