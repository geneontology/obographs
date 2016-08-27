package org.geneontology.obographs.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.geneontology.obographs.io.OgSchemaGenerator;
import org.geneontology.obographs.model.Graph;
import org.geneontology.obographs.model.GraphDocument;
import org.junit.Test;

public class OgSchemaGeneratorTest {

	@Test
	public void test() throws IOException {
		String s = OgSchemaGenerator.makeSchema(GraphDocument.class);
		//System.out.println(s);
		writeSchema("bbop-graph-schema.json", s);

	}
	
	protected void writeSchema(String fn, String info) throws IOException {
		FileUtils.writeStringToFile(new File("target/"+fn), info);
	}

}
