package org.geneontology.obographs.core.io;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class OgJsonGenerator {

	private OgJsonGenerator() {
		// static utility class
	}

	public static String render(Object obj) throws JsonProcessingException {
		ObjectWriter objectWriter = newObjectWriter();
		return objectWriter.writeValueAsString(obj);
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
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
		return mapper.writerWithDefaultPrettyPrinter();
	}
}
