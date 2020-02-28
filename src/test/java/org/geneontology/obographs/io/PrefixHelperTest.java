package org.geneontology.obographs.io;

import com.github.jsonldjava.core.Context;
import org.junit.Test;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

        assertEquals("Check GO prefix",
                "http://purl.obolibrary.org/obo/GO_",
                context.getPrefixes(false).get("GO"));
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
        assertEquals("Check JSON prefixes", expected, ioh.getPrefixes());
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
        assertEquals("Check no prefixes", expected, ioh.getPrefixes());

        ioh.addPrefix("foo", "http://example.com#");
        expected.put("foo", "http://example.com#");
        assertEquals("Check foo prefix", expected, ioh.getPrefixes());

        String json = String.format("{%n"
                    + "  \"@context\" : {%n"
                    + "    \"foo\" : \"http://example.com#\"%n"
                    + "  }%n"
                    + "}");
        assertEquals("Check JSON-LD", json, ioh.getContextString());

        ioh.addPrefix("bar: http://example.com#");
        expected.put("bar", "http://example.com#");
        assertEquals("Check no prefixes", expected, ioh.getPrefixes());
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

        assertEquals("Check GO CURIE",
                "http://purl.obolibrary.org/obo/GO_12345",
                pm.getIRI("GO:12345").toString());
    }

}
