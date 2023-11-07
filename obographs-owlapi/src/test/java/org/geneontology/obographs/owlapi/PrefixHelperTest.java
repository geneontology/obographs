package org.geneontology.obographs.owlapi;

import com.github.jsonldjava.core.Context;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrefixHelperTest {

    /**
     * Test getting the default context.
     *
     * @throws IOException on file problem
     */
    @Test
    public void testContext() throws IOException {
        PrefixHelper ioh = new PrefixHelper();
        Context context = ioh.getContext();

        assertEquals(
                "http://purl.obolibrary.org/obo/GO_",
                context.getPrefixes(false).get("GO"), "Check GO prefix");
    }

    /**
     * Test fancier JSON-LD contexts.
     *
     * @throws IOException on file problem
     */
    @Test
    public void testContextHandling() throws IOException {
        String json = "{\n"
                    + "  \"@context\" : {\n"
                    + "    \"foo\" : \"http://example.com#\",\n"
                    + "    \"bar\" : {\n"
                    + "      \"@id\": \"http://example.com#\",\n"
                    + "      \"@type\": \"@id\"\n"
                    + "    }\n"
                    + "  }\n"
                    + "}";

        PrefixHelper ioh = new PrefixHelper();
        Context context = ioh.parseContext(json);
        ioh.setContext(context);

        Map<String, String> expected = new HashMap<String, String>();
        expected.put("foo", "http://example.com#");
        expected.put("bar", "http://example.com#");
        assertEquals(expected, ioh.getPrefixes(), "Check JSON prefixes");
    }

    /**
     * Test prefix maps.
     *
     * @throws IOException on file problem
     */
    @Test
    public void testPrefixHandling() throws IOException {
        PrefixHelper ioh = new PrefixHelper(false);
        Map<String, String> expected = new HashMap<String, String>();
        assertEquals(expected, ioh.getPrefixes(), "Check no prefixes");

        ioh.addPrefix("foo", "http://example.com#");
        expected.put("foo", "http://example.com#");
        assertEquals(expected, ioh.getPrefixes(), "Check foo prefix");

        String json = String.format("{%n"
                    + "  \"@context\" : {%n"
                    + "    \"foo\" : \"http://example.com#\"%n"
                    + "  }%n"
                    + "}");
        assertEquals(json, ioh.getContextString(), "Check JSON-LD");

        ioh.addPrefix("bar: http://example.com#");
        expected.put("bar", "http://example.com#");
        assertEquals(expected, ioh.getPrefixes(), "Check no prefixes");
    }

    /**
     * Test the default prefix manager.
     *
     * @throws IOException on file problem
     */
    @Test
    public void testPrefixManager() throws IOException {
        PrefixHelper ioh = new PrefixHelper();
        DefaultPrefixManager pm = ioh.getPrefixManager();

        assertEquals("http://purl.obolibrary.org/obo/GO_12345",
                pm.getIRI("GO:12345").toString(),
                "Check GO CURIE");
    }

}
