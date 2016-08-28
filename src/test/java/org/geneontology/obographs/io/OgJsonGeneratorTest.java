package org.geneontology.obographs.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.model.Graph;
import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.model.GraphDocumentTest;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

public class OgJsonGeneratorTest {

    @Test
    public void test() throws IOException {
        GraphDocument d = GraphDocumentTest.build();
  
        String s = OgJsonGenerator.render(d);
        FileUtils.writeStringToFile(new File("target/simple-example.json"), s);
    }

    

}
