package org.geneontology.obographs.core.io;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class OgYamlGenerator {

	private OgYamlGenerator() {
		// static utility class
	}

	public static String render(Object obj) throws JsonProcessingException {
		ObjectWriter writer = newObjectWriter();
		return writer.writeValueAsString(obj);
	}
	
	public static void write(OutputStream outputStream, Object obj) throws IOException {
		ObjectWriter objectWriter = newObjectWriter();
		objectWriter.writeValue(outputStream, obj);
	}

	public static void write(Writer writer, Object obj) throws IOException {
		ObjectWriter objectWriter = newObjectWriter();
		objectWriter.writeValue(writer, obj);
	}

	public static void write(File file, Object obj) throws IOException {
		ObjectWriter objectWriter = newObjectWriter();
		objectWriter.writeValue(file, obj);
	}

	private static ObjectWriter newObjectWriter() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
		return mapper.writerWithDefaultPrettyPrinter();
	}

}
