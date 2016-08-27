package org.geneontology.obographs.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

public class OgSchemaGenerator {

	public static String makeSchema(Class c) throws IOException {
		
		ObjectMapper m = new ObjectMapper();
		SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
		m.acceptJsonFormatVisitor(m.constructType(c), visitor);
		JsonSchema jsonSchema = visitor.finalSchema();
		String s = m.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
		return s;
	}

}
