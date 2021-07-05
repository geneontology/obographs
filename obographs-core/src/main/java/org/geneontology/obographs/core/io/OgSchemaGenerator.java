package org.geneontology.obographs.core.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

import java.io.IOException;

public class OgSchemaGenerator {

	private OgSchemaGenerator() {
		// static utility class
	}

	public static String makeSchema(Class<?> c) throws IOException {
		ObjectMapper m = new ObjectMapper();
		SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
		m.acceptJsonFormatVisitor(m.constructType(c), visitor);
		JsonSchema jsonSchema = visitor.finalSchema();
		return m.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
	}

}
