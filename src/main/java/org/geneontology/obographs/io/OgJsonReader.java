package org.geneontology.obographs.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geneontology.obographs.model.GraphDocument;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class OgJsonReader {

	public static GraphDocument readFile(String fileName) throws IOException {
		return readFile(new File(fileName));
	}

	public static GraphDocument readFile(File file) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(file, GraphDocument.class);
	}

	public static GraphDocument readInputStream(InputStream stream) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(stream, GraphDocument.class);
	}
	
}
