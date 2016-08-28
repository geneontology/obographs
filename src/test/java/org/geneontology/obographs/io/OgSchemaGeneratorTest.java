package org.geneontology.obographs.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.io.OgSchemaGenerator;
import org.geneontology.obographs.model.Graph;
import org.geneontology.obographs.model.GraphDocument;
import org.geneontology.obographs.model.Meta;
import org.junit.Test;

public class OgSchemaGeneratorTest {

	@Test
	public void test() throws IOException {
		//System.out.println(s);
        writeSchema(GraphDocument.class, "obographs-schema.json");
        writeSchema(Graph.class, "subschemas/obographs-graph-schema.json");
        writeSchema(Meta.class, "subschemas/obographs-meta-schema.json");
	}
	
	protected void writeSchema(Class c, String fn) throws IOException {
        String s = OgSchemaGenerator.makeSchema(c);
        FileUtils.writeStringToFile(new File("target/"+fn), s);
        FileUtils.writeStringToFile(new File("schema/"+fn), s);
	}

}
