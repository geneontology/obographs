package org.geneontology.obographs.io;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public class OgYamlGenerator {
	
	public static String render(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.registerModule(new GuavaModule());
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
		ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
		return writer.writeValueAsString(obj);
	}

}
