package org.geneontology.obographs.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.geneontology.obographs.model.Graph;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OgJsonReader {

	public static Graph readFile(String fileName) throws IOException {
		return readFile(new File(fileName));
	}

	public static Graph readFile(File file) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(file, Graph.class);
	}

	public static Graph readInputStream(InputStream stream) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(stream, Graph.class);
	}
	
}
