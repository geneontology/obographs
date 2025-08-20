package org.geneontology.obographs.owlapi;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.core.io.OgJsonGenerator;
import org.geneontology.obographs.core.io.OgJsonReader;
import org.geneontology.obographs.core.io.OgYamlGenerator;
import org.geneontology.obographs.core.io.OgYamlReader;
import org.geneontology.obographs.core.model.*;
import org.geneontology.obographs.core.model.PropertyType;
import org.geneontology.obographs.core.model.meta.BasicPropertyValue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.geneontology.obographs.core.model.RdfType.*;
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
    @Test
    @Disabled("Only to be run manually when examples directory needs updating due to change in expected obograph file output")
    void createExampleFiles() throws Exception {
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

            List<String> formats = List.of(".json", ".yaml");
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
        return switch (format) {
            case ".json" -> OgJsonReader.readFile(newFilePath.toFile());
            case ".yaml" -> OgYamlReader.readFile(newFilePath.toFile());
            default -> throw new IllegalArgumentException("Format '" + format + "' not recognised");
        };
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
                    System.out.println("Mismatch at " + newFile + " line " + line + " when comparing to " + originalFile);
                    System.out.println("Exp: " + line1);
                    System.out.println("Got: " + line2);
                    fail("Mismatch at " + newFile + " line " + line + " when comparing to " + originalFile);
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
        String axiom = """
                <?xml version="1.0"?>
                <rdf:RDF xmlns="http://purl.obolibrary.org/obo/uberon.owl#"
                     xml:base="http://purl.obolibrary.org/obo/uberon.owl"
                     xmlns:obo="http://purl.obolibrary.org/obo/"
                     xmlns:oboInOwl="http://www.geneontology.org/formats/oboInOwl#"
                     xmlns:owl="http://www.w3.org/2002/07/owl#"
                     xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                    <owl:Ontology rdf:about="http://purl.obolibrary.org/obo/uberon.owl">
                        <owl:versionIRI rdf:resource="http://purl.obolibrary.org/obo/uberon/releases/2022-12-13/uberon.owl"/>
                    </owl:Ontology>
                    <owl:Axiom>
                        <owl:annotatedSource rdf:resource="http://purl.obolibrary.org/obo/UBERON_0009551"/>
                        <owl:annotatedProperty rdf:resource="http://www.w3.org/2002/07/owl#equivalentClass"/>
                        <owl:annotatedTarget>
                            <owl:Class>
                                <owl:intersectionOf rdf:parseType="Collection">
                                    <rdf:Description rdf:about="http://purl.obolibrary.org/obo/UBERON_0002529"/>
                                    <owl:Restriction>
                                        <owl:onProperty rdf:resource="http://purl.obolibrary.org/obo/BFO_0000050"/>
                                        <owl:someValuesFrom rdf:resource="http://purl.obolibrary.org/obo/UBERON_0002544"/>
                                    </owl:Restriction>
                                    <owl:Restriction>
                                        <owl:onProperty rdf:resource="http://purl.obolibrary.org/obo/BFO_0000051"/>
                                        <owl:someValuesFrom rdf:resource="http://purl.obolibrary.org/obo/UBERON_0004300"/>
                                    </owl:Restriction>
                                    <owl:Restriction>
                                        <owl:onProperty rdf:resource="http://purl.obolibrary.org/obo/BFO_0000051"/>
                                        <owl:someValuesFrom>
                                            <owl:Restriction>
                                                <owl:onProperty rdf:resource="http://purl.obolibrary.org/obo/BFO_0000050"/>
                                                <owl:someValuesFrom rdf:resource="http://purl.obolibrary.org/obo/UBERON_0009768"/>
                                            </owl:Restriction>
                                        </owl:someValuesFrom>
                                    </owl:Restriction>
                                </owl:intersectionOf>
                            </owl:Class>
                        </owl:annotatedTarget>
                        <oboInOwl:source>cjm</oboInOwl:source>
                    </owl:Axiom>
                </rdf:RDF>
                """;
        GraphDocument graphDocument = generateGraphDocument(axiom);
        System.out.println(OgYamlGenerator.render(graphDocument));
    }

    /**
     * <a href="https://github.com/geneontology/obographs/issues/90">...</a>
     */
    @Test
    public void testClassDeclarationsWithoutFurtherAssertionsShouldBeIncludedInOboGraph() throws Exception {
        String owlFile = """
                         Prefix(:=<http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544#>)
                         Prefix(owl:=<http://www.w3.org/2002/07/owl#>)
                         Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)
                         Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)
                         Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)
                         Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)
                         
                         
                         Ontology(<http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544>
                         
                         Declaration(Class(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000060>))
                         )
                         """;

        GraphDocument actual = generateGraphDocument(owlFile);

        Graph.Builder graphBuilder = new Graph.Builder().id("http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544")
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000060").rdfType(CLASS).build());
        assertThat(actual, equalTo(new GraphDocument.Builder().addGraph(graphBuilder.build()).build()));
    }

    /**
     * https://github.com/geneontology/obographs/issues/93
     */
    @Test
    public void testComplexPropertyExpressionLogsWarning() throws Exception {
        var rdf = """
                  <?xml version="1.0"?>
                  <rdf:RDF xmlns="http://purl.obolibrary.org/obo/fail-expression.owl#"
                       xml:base="http://purl.obolibrary.org/obo/fail-expression.owl"
                       xmlns:owl="http://www.w3.org/2002/07/owl#"
                       xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                       xmlns:xml="http://www.w3.org/XML/1998/namespace"
                       xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
                       xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#">
                      <owl:Ontology rdf:about="http://purl.obolibrary.org/obo/fail-expression.owl">
                      </owl:Ontology>
                  
                      <owl:Class rdf:about="http://purl.obolibrary.org/obo/OBI_0000260">
                 
                          <rdfs:subClassOf>
                              <owl:Restriction>
                                  <owl:onProperty>
                                      <rdf:Description>
                                          <owl:inverseOf rdf:resource="http://purl.obolibrary.org/obo/COB_0000087"/>
                                      </rdf:Description>
                                  </owl:onProperty>
                                  <owl:allValuesFrom rdf:resource="http://purl.obolibrary.org/obo/COB_0000082"/>
                              </owl:Restriction>
                          </rdfs:subClassOf>
                      </owl:Class>
                  </rdf:RDF>
                  """;

        GraphDocument graphDocument = generateGraphDocument(rdf);
        Graph graph = new Graph.Builder().id("http://purl.obolibrary.org/obo/fail-expression.owl")
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/OBI_0000260").rdfType(CLASS).build())
                .build();
        assertThat(graphDocument, equalTo(new GraphDocument.Builder().addGraph(graph).build()));
    }

    /**
     * https://github.com/geneontology/obographs/issues/71
     */
    @Test
    public void testDataPropertyAssertionHandling() throws Exception {
        String ttl = """
                         @prefix skos: <http://www.w3.org/2004/02/skos/core#> .
                         @prefix owl:  <http://www.w3.org/2002/07/owl#> .
                         @prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .
                         @prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
                         
                         <http://foo.com/bar> a owl:Class ;
                             skos:prefLabel  "some node"@en ;
                             <http://foo.com/baz> "test property"^^xsd:string .
                         
                         <http://foo.com/baz> a owl:DatatypeProperty ;
                             rdfs:label "test label" ;
                             rdfs:comment "test comment" .
                         """;

        GraphDocument graphDocument = generateGraphDocument(ttl);
//        System.out.println(OgYamlGenerator.render(graphDocument));
        Graph graph = new Graph.Builder()
                .addNode(new Node.Builder().id("http://foo.com/bar").rdfType(CLASS).meta(new Meta.Builder().addBasicPropertyValue(new BasicPropertyValue.Builder().pred("http://www.w3.org/2004/02/skos/core#prefLabel").val("some node").build()).build()).build())
                .addNode(new Node.Builder().id("http://foo.com/baz").rdfType(PROPERTY).propertyType(PropertyType.DATA).label("test label").meta(new Meta.Builder().addComment("test comment").build()).build())
                .build();
        assertThat(graphDocument, equalTo(new GraphDocument.Builder().addGraph(graph).build()));
    }

    /**
     * <a href="https://github.com/geneontology/obographs/issues/65">...</a>
     */
    @Test
    public void testPropertiesHaveTypes() throws Exception{
        String owl = """
                     Prefix(:=<http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544#>)
                     Prefix(owl:=<http://www.w3.org/2002/07/owl#>)
                     Prefix(rdf:=<http://www.w3.org/1999/02/22-rdf-syntax-ns#>)
                     Prefix(xml:=<http://www.w3.org/XML/1998/namespace>)
                     Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)
                     Prefix(rdfs:=<http://www.w3.org/2000/01/rdf-schema#>)
                     
                     
                     Ontology(<http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544>
                     
                     Declaration(Class(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000059>))
                     Declaration(AnnotationProperty(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000060>))
                     Declaration(ObjectProperty(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000061>))
                     Declaration(DataProperty(<http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000062>))
                     )
                     """;
        GraphDocument graphDocument = generateGraphDocument(owl);
        System.out.println(OgYamlGenerator.render(graphDocument));
        Graph graph = new Graph.Builder().id("http://www.semanticweb.org/matentzn/ontologies/2021/11/untitled-ontology-544")
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000059").rdfType(CLASS).build())
                // owlapi sort order - don't change this!
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000061").rdfType(PROPERTY).propertyType(PropertyType.OBJECT).build())
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000062").rdfType(PROPERTY).propertyType(PropertyType.DATA).build())
                .addNode(new Node.Builder().id("http://purl.obolibrary.org/obo/upheno/workshop2021/TMP_0000060").rdfType(PROPERTY).propertyType(PropertyType.ANNOTATION).build())
                .build();
        assertThat(graphDocument, equalTo(new GraphDocument.Builder().addGraph(graph).build()));
    }
}
