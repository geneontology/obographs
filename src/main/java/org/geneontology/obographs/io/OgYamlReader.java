package org.geneontology.obographs.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.geneontology.obographs.model.GraphDocument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class OgYamlReader {
	
	public static GraphDocument readFile(String fileName) throws IOException {
		return readFile(new File(fileName));
	}
	
	public static GraphDocument readFile(File file) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		objectMapper.registerModule(new GuavaModule());
		return objectMapper.readValue(file, GraphDocument.class);
	}
	
	public static GraphDocument readInputStream(InputStream stream) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
		objectMapper.registerModule(new GuavaModule());
		return objectMapper.readValue(stream, GraphDocument.class);
	}

}
