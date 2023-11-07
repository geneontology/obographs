package org.geneontology.obographs.core.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.geneontology.obographs.core.model.GraphDocument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class OgYamlReader {

	private OgYamlReader() {
		// static utility class
	}

	public static GraphDocument readFile(String fileName) throws IOException {
		return readFile(new File(fileName));
	}

	public static GraphDocument readFile(File file) throws IOException {
		ObjectMapper objectMapper = newObjectMapper();
		return objectMapper.readValue(file, GraphDocument.class);
	}

	public static GraphDocument readInputStream(InputStream stream) throws IOException {
		ObjectMapper objectMapper = newObjectMapper();
		return objectMapper.readValue(stream, GraphDocument.class);
	}

	public static GraphDocument read(Reader reader) throws IOException {
		ObjectMapper objectMapper = newObjectMapper();
		return objectMapper.readValue(reader, GraphDocument.class);
	}

	private static ObjectMapper newObjectMapper() {
		return new ObjectMapper(new YAMLFactory());
	}
}
