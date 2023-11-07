package org.geneontology.obographs.owlapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.core.io.OgJsonGenerator;
import org.geneontology.obographs.core.io.OgJsonReader;
import org.geneontology.obographs.core.io.OgYamlGenerator;
import org.geneontology.obographs.core.io.OgYamlReader;
import org.geneontology.obographs.core.model.*;
import org.geneontology.obographs.core.model.AbstractNode.PropertyType;
import org.geneontology.obographs.core.model.meta.BasicPropertyValue;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.geneontology.obographs.core.model.AbstractNode.RDFTYPES.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Generates json from OWL and tests the content returned for equality.
 *
 * @author cjm
 *
 */
public class FromOwlTest {

    private final Path examplesPath = Paths.get("../examples");

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

    private void compareFilesLineByLine(Path originalFile, Path newFile) throws IOException {
        try (BufferedReader br1 = Files.newBufferedReader(originalFile);
             BufferedReader br2 = Files.newBufferedReader(newFile)) {
            String line1 = br1.readLine();
            String line2 = br2.readLine();
            int line = 1;
            while (line1 != null && line2 != null) {
                if (!line1.equals(line2)) {
                    System.out.println("Mismatch at line: " + line);
                    System.out.println("Exp: " + line1);
                    System.out.println("Got: " + line2);
                    fail("Mismatch at " + newFile + " line " + line);
                }
                line++;
                line1 = br1.readLine();
                line2 = br2.readLine();
            }
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

    private static GraphDocument generateGraphDocument(String ontologyFile) throws OWLOntologyCreationException {
        OWLOntologyManager m = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = m.loadOntologyFromOntologyDocument(new ByteArrayInputStream(ontologyFile.getBytes(StandardCharsets.UTF_8)));
        return new FromOwl().generateGraphDocument(ontology);
    }

    @Test
    public void testMissingNestedRestrictions() throws Exception {
        String axiom = "<?xml version=\"1.0\"?>\n" +
                "<rdf:RDF xmlns=\"http://purl.obolibrary.org/obo/uberon.owl#\"\n" +
                "     xml:base=\"http://purl.obolibrary.org/obo/uberon.owl\"\n" +
                "     xmlns:obo=\"http://purl.obolibrary.org/obo/\"\n" +
                "     xmlns:oboInOwl=\"http://www.geneontology.org/formats/oboInOwl#\"\n" +
                "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
                "    <owl:Ontology rdf:about=\"http://purl.obolibrary.org/obo/uberon.owl\">\n" +
                "        <owl:versionIRI rdf:resource=\"http://purl.obolibrary.org/obo/uberon/releases/2022-12-13/uberon.owl\"/>\n" +
                "    </owl:Ontology>\n" +
                "    \n<owl:Axiom>\n" +
                "        <owl:annotatedSource rdf:resource=\"http://purl.obolibrary.org/obo/UBERON_0009551\"/>\n" +
                "        <owl:annotatedProperty rdf:resource=\"http://www.w3.org/2002/07/owl#equivalentClass\"/>\n" +
                "        <owl:annotatedTarget>\n" +
                "            <owl:Class>\n" +
                "                <owl:intersectionOf rdf:parseType=\"Collection\">\n" +
                "                    <rdf:Description rdf:about=\"http://purl.obolibrary.org/obo/UBERON_0002529\"/>\n" +
                "                    <owl:Restriction>\n" +
                "                        <owl:onProperty rdf:resource=\"http://purl.obolibrary.org/obo/BFO_0000050\"/>\n" +
                "                        <owl:someValuesFrom rdf:resource=\"http://purl.obolibrary.org/obo/UBERON_0002544\"/>\n" +
                "                    </owl:Restriction>\n" +
                "                    <owl:Restriction>\n" +
                "                        <owl:onProperty rdf:resource=\"http://purl.obolibrary.org/obo/BFO_0000051\"/>\n" +
                "                        <owl:someValuesFrom rdf:resource=\"http://purl.obolibrary.org/obo/UBERON_0004300\"/>\n" +
                "                    </owl:Restriction>\n" +
                "                    <owl:Restriction>\n" +
                "                        <owl:onProperty rdf:resource=\"http://purl.obolibrary.org/obo/BFO_0000051\"/>\n" +
                "                        <owl:someValuesFrom>\n" +
                "                            <owl:Restriction>\n" +
                "                                <owl:onProperty rdf:resource=\"http://purl.obolibrary.org/obo/BFO_0000050\"/>\n" +
                "                                <owl:someValuesFrom rdf:resource=\"http://purl.obolibrary.org/obo/UBERON_0009768\"/>\n" +
                "                            </owl:Restriction>\n" +
                "                        </owl:someValuesFrom>\n" +
                "                    </owl:Restriction>\n" +
                "                </owl:intersectionOf>\n" +
                "            </owl:Class>\n" +
                "        </owl:annotatedTarget>\n" +
                "        <oboInOwl:source>cjm</oboInOwl:source>\n" +
                "    </owl:Axiom>\n" +
                "</rdf:RDF>";
        GraphDocument graphDocument = generateGraphDocument(axiom);
        System.out.println(OgYamlGenerator.render(graphDocument));
    }

    /**
     * <a href="https://github.com/geneontology/obographs/issues/90">...</a>
     */
    @Test
    public void testClassDeclarationsWithoutFurtherAssertionsShouldBeIncludedInOboGraph() throws Exception {
        String owlFile = "Prefix(:=<http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544#>)\n" +
                         "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n" +
                         "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n" +
                         "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n" +
                         "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" +
                         "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" +
                         "\n" +
                         "\n" +
                         "Ontology(<http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544>\n" +
                         "\n" +
                         "Declaration(Class(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000060>))\n" +
                         ")";

        GraphDocument actual = generateGraphDocument(owlFile);

        Graph.Builder graphBuilder = new Graph.Builder().id("http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544")
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000060").type(CLASS).build());
        assertThat(actual, equalTo(new GraphDocument.Builder().addGraph(graphBuilder.build()).build()));
    }

    /**
     * https://github.com/geneontology/obographs/issues/93
     */
    @Test
    public void testComplexPropertyExpressionLogsWarning() throws Exception {
        var rdf = "<?xml version=\"1.0\"?>\n" +
                  "<rdf:RDF xmlns=\"http://purl.obolibrary.org/obo/fail-expression.owl#\"\n" +
                  "     xml:base=\"http://purl.obolibrary.org/obo/fail-expression.owl\"\n" +
                  "     xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n" +
                  "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
                  "     xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n" +
                  "     xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n" +
                  "     xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\">\n" +
                  "    <owl:Ontology rdf:about=\"http://purl.obolibrary.org/obo/fail-expression.owl\">\n" +
                  "    </owl:Ontology>\n" +
                  "    \n" +
                  "    <owl:Class rdf:about=\"http://purl.obolibrary.org/obo/OBI_0000260\">\n" +
                  "        \n" +
                  "        <rdfs:subClassOf>\n" +
                  "            <owl:Restriction>\n" +
                  "                <owl:onProperty>\n" +
                  "                    <rdf:Description>\n" +
                  "                        <owl:inverseOf rdf:resource=\"http://purl.obolibrary.org/obo/COB_0000087\"/>\n" +
                  "                    </rdf:Description>\n" +
                  "                </owl:onProperty>\n" +
                  "                <owl:allValuesFrom rdf:resource=\"http://purl.obolibrary.org/obo/COB_0000082\"/>\n" +
                  "            </owl:Restriction>\n" +
                  "        </rdfs:subClassOf>\n" +
                  "    </owl:Class>\n" +
                  "   \n" +
                  "</rdf:RDF>";

        GraphDocument graphDocument = generateGraphDocument(rdf);
        Graph graph = new Graph.Builder().id("http://purl.obolibrary.org/obo/fail-expression.owl")
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/OBI_0000260").type(CLASS).build())
                .build();
        assertThat(graphDocument, equalTo(new GraphDocument.Builder().addGraph(graph).build()));
    }

    /**
     * https://github.com/geneontology/obographs/issues/71
     */
    @Test
    public void testDataPropertyAssertionHandling() throws Exception {
        String ttl = "@prefix skos: <http://www.w3.org/2004/02/skos/core#> .\n" +
                         "@prefix owl:  <http://www.w3.org/2002/07/owl#> .\n" +
                         "@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
                         "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
                         "\n" +
                         "<http://foo.com/bar> a owl:Class ;\n" +
                         "        skos:prefLabel \"\"\"some node\"\"\"@en ;\n" +
                         "        <http://foo.com/baz> \"\"\"test property\"\"\"^^xsd:string ;\n" +
                         ".\n" +
                         "\n" +
                         "<http://foo.com/baz> a owl:DatatypeProperty ;\n" +
                         "    rdfs:label \"\"\"test label\"\"\";\n" +
                         "    rdfs:comment \"\"\"test comment\"\"\" .";

        GraphDocument graphDocument = generateGraphDocument(ttl);
//        System.out.println(OgYamlGenerator.render(graphDocument));
        Graph graph = new Graph.Builder()
                .addNode(new Node.Builder().id("http://foo.com/bar").type(CLASS).meta(new Meta.Builder().addBasicPropertyValue(new BasicPropertyValue.Builder().pred("http://www.w3.org/2004/02/skos/core#prefLabel").val("some node").build()).build()).build())
                .addNode(new Node.Builder().id("http://foo.com/baz").type(PROPERTY).propertyType(PropertyType.DATA).label("test label").meta(new Meta.Builder().addComment("test comment").build()).build())
                .build();
        assertThat(graphDocument, equalTo(new GraphDocument.Builder().addGraph(graph).build()));
    }

    /**
     * <a href="https://github.com/geneontology/obographs/issues/65">...</a>
     */
    @Test
    public void testPropertiesHaveTypes() throws Exception{
        String owl = "Prefix(:=<http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544#>)\n" +
                     "Prefix(owl:=<http://www.w3.org/2002/07/owl#>)\n" +
                     "Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)\n" +
                     "Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)\n" +
                     "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n" +
                     "Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)\n" +
                     "\n" +
                     "\n" +
                     "Ontology(<http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544>\n" +
                     "\n" +
                     "Declaration(Class(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000059>))\n" +
                     "Declaration(AnnotationProperty(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000060>))\n" +
                     "Declaration(ObjectProperty(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000061>))\n" +
                     "Declaration(DataProperty(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000062>))\n" +
                     ")";
        GraphDocument graphDocument = generateGraphDocument(owl);
        System.out.println(OgYamlGenerator.render(graphDocument));
        Graph graph = new Graph.Builder().id("http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544")
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000059").type(CLASS).build())
                // owlapi sort order - don't change this!
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000061").type(PROPERTY).propertyType(PropertyType.OBJECT).build())
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000062").type(PROPERTY).propertyType(PropertyType.DATA).build())
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000060").type(PROPERTY).propertyType(PropertyType.ANNOTATION).build())
                .build();
        assertThat(graphDocument, equalTo(new GraphDocument.Builder().addGraph(graph).build()));
    }
}
