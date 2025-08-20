package org.geneontology.obographs.core.io;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder;
import org.geneontology.obographs.core.model.GraphDocument;
import org.yaml.snakeyaml.LoaderOptions;

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
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
		// This needs to be set way above the default of 3 * 1024 * 1024; (3 MB) as hp.yaml is too large with the
		// addition of the BasicPropertyValueMeta
		LoaderOptions loaderOptions = new LoaderOptions();
		loaderOptions.setCodePointLimit(100 * 1024 * 1024); // 100MB
		return new ObjectMapper(new YAMLFactoryBuilder(new YAMLFactory()).loaderOptions(loaderOptions).build());
	}
}
