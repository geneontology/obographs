package org.geneontology.obographs.owlapi;

import com.github.jsonldjava.core.Context;
import com.github.jsonldjava.core.JsonLdApi;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides convenience methods for working with JSON LD contexts and prefixes
 *
 * Taken from ROBOT
 * @author <a href="mailto:james@overton.ca">James A. Overton</a>
 */
public class PrefixHelper {
    /**
     * Logger.
     */
    private static final Logger logger =
        LoggerFactory.getLogger(PrefixHelper.class);

   
    /**
     * Path to default context as a resource.
     */
    private static String defaultContextPath = "/obo_context.jsonld";

    /**
     * Store the current JSON-LD context.
     */
    private Context context = new Context();

    /**
     * Create a new IOHelper with the default prefixes.
     */
    public PrefixHelper() {
        try {
            setContext(getDefaultContext());
        } catch (IOException e) {
            logger.warn("Could not load default prefixes.");
            logger.warn(e.getMessage());
        }
    }

    /**
     * Create a new IOHelper with or without the default prefixes.
     *
     * @param defaults false if defaults should not be used
     */
    public PrefixHelper(boolean defaults) {
        try {
            if (defaults) {
                setContext(getDefaultContext());
            } else {
                setContext();
            }
        } catch (IOException e) {
            logger.warn("Could not load default prefixes.");
            logger.warn(e.getMessage());
        }
    }

    /**
     * Create a new IOHelper with the specified prefixes.
     *
     * @param map the prefixes to use
     */
    public PrefixHelper(Map<String, Object> map) {
        setContext(map);
    }

    /**
     * Create a new IOHelper with prefixes from a file path.
     *
     * @param path to a JSON-LD file with a @context
     */
    public PrefixHelper(String path) {
        try {
            String jsonString = FileUtils.readFileToString(new File(path));
            setContext(jsonString);
        } catch (IOException e) {
            logger.warn("Could not load prefixes from {}", path, e);
        }
    }

    /**
     * Create a new IOHelper with prefixes from a file.
     *
     * @param file a JSON-LD file with a @context
     */
    public PrefixHelper(File file) {
        try {
            String jsonString = FileUtils.readFileToString(file);
            setContext(jsonString);
        } catch (IOException e) {
            logger.warn("Could not load prefixes from {}", file, e);
        }
    }

    /**
     * Try to guess the location of the catalog.xml file.
     * Looks in the directory of the given ontology file for a catalog file.
     *
     * @param ontologyFile the
     * @return the guessed catalog File; may not exist!
     */
    public File guessCatalogFile(File ontologyFile) {
        String path = ontologyFile.getParent();
        String catalogPath = "catalog-v001.xml";
        if (path != null) {
            catalogPath = path + "/catalog-v001.xml";
        }
        return new File(catalogPath);
    }


    /**
     * Given a term string, use the current prefixes to create an IRI.
     *
     * @param term the term to convert to an IRI
     * @return the new IRI
     */
    public IRI createIRI(String term) {
        if (term == null) {
            return null;
        }

        try {
            // This is stupid, because better methods aren't public.
            // We create a new JSON map and add one entry
            // with the term as the key and some string as the value.
            // Then we run the JsonLdApi to expand the JSON map
            // in the current context, and just grab the first key.
            // If everything worked, that key will be our expanded iri.
            Map<String, Object> jsonMap = new HashMap<String, Object>();
            jsonMap.put(term, "ignore this string");
            Object expanded = new JsonLdApi().expand(context, jsonMap);
            String result = ((Map<String, Object>) expanded)
                .keySet().iterator().next();
            if (result != null) {
                return IRI.create(result);
            }
        } catch (Exception e) {
            logger.warn("Could not create IRI for {}", term, e);
        }
        return null;
    }

  
    /**
     * Load a map of prefixes from the "@context" of a JSON-LD string.
     *
     * @param jsonString the JSON-LD string
     * @return a map from prefix name strings to prefix IRI strings
     * @throws IOException on any problem
     */
    public static Context parseContext(String jsonString) throws IOException {
        try {
            Object jsonObject = JsonUtils.fromString(jsonString);
            if (!(jsonObject instanceof Map)) {
                return null;
            }
            Map<String, Object> jsonMap = (Map<String, Object>) jsonObject;
            if (!jsonMap.containsKey("@context")) {
                return null;
            }
            Object jsonContext = jsonMap.get("@context");
            return new Context().parse(jsonContext);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Get a copy of the default context.
     *
     * @return a copy of the current context
     * @throws IOException if default context file cannot be read
     */
    public Context getDefaultContext() throws IOException {
        InputStream stream =
            PrefixHelper.class.getResourceAsStream(defaultContextPath);
        String jsonString = IOUtils.toString(stream);
        return parseContext(jsonString);
    }

    /**
     * Get a copy of the current context.
     *
     * @return a copy of the current context
     */
    public Context getContext() {
        return this.context.clone();
    }

    /**
     * Set an empty context.
     */
    public void setContext() {
        this.context = new Context();
    }

    /**
     * Set the current JSON-LD context to the given context.
     *
     * @param context the new JSON-LD context
     */
    public void setContext(Context context) {
        if (context == null) {
            setContext();
        } else {
            this.context = context;
        }
    }

    /**
     * Set the current JSON-LD context to the given context.
     *
     * @param jsonString the new JSON-LD context as a JSON string
     */
    public void setContext(String jsonString) {
        try {
            this.context = parseContext(jsonString);
        } catch (Exception e) {
            logger.warn("Could not set context from JSON", e);
        }
    }

    /**
     * Set the current JSON-LD context to the given map.
     *
     * @param map a map of strings for the new JSON-LD context
     */
    public void setContext(Map<String, Object> map) {
        try {
            this.context = new Context().parse(map);
        } catch (Exception e) {
            logger.warn("Could not set context {}", map, e);
        }
    }



    /**
     * Make an OWLAPI DefaultPrefixManager from a map of prefixes.
     *
     * @param prefixes a map from prefix name strings to prefix IRI strings
     * @return a new DefaultPrefixManager
     */
    public static DefaultPrefixManager makePrefixManager(Map<String, String> prefixes) {
        DefaultPrefixManager pm = new DefaultPrefixManager();
        for (Map.Entry<String, String> entry: prefixes.entrySet()) {
            pm.setPrefix(entry.getKey() + ":", entry.getValue());
        }
        return pm;
    }

    /**
     * Get a prefix manager with the current prefixes.
     *
     * @return a new DefaultPrefixManager
     */
    public DefaultPrefixManager getPrefixManager() {
        return makePrefixManager(context.getPrefixes(false));
    }

    /**
     * Add a prefix mapping as a single string "foo: http://example.com#".
     *
     * @param combined both prefix and target
     * @throws IllegalArgumentException on malformed input
     */
    public void addPrefix(String combined) throws IllegalArgumentException {
        String[] results = combined.split(":", 2);
        if (results.length < 2) {
            throw new IllegalArgumentException(
                    "Invalid prefix string: " + combined);
        }
        addPrefix(results[0], results[1]);
    }

    /**
     * Add a prefix mapping to the current JSON-LD context,
     * as a prefix string and target string.
     * Rebuilds the context.
     *
     * @param prefix the short prefix to add; should not include ":"
     * @param target the IRI string that is the target of the prefix
     */
    public void addPrefix(String prefix, String target) {
        try {
            context.put(prefix.trim(), target.trim());
            context.remove("@base");
            setContext((Map<String, Object>) context);
        } catch (Exception e) {
            logger.warn("Could not load add prefix \"{}\" \"{}\"",
                    prefix, target, e);
        }
    }

    /**
     * Get a copy of the current prefix map.
     *
     * @return a copy of the current prefix map
     */
    public Map<String, String> getPrefixes() {
        return this.context.getPrefixes(false);
    }

    /**
     * Set the current prefix map.
     *
     * @param map the new map of prefixes to use
     */
    public void setPrefixes(Map<String, Object> map) {
        setContext(map);
    }

    /**
     * Return the current prefixes as a JSON-LD string.
     *
     * @return the current prefixes as a JSON-LD string
     * @throws IOException on any error
     */
    public String getContextString() throws IOException {
        try {
            Object compact = JsonLdProcessor.compact(
                    JsonUtils.fromString("{}"),
                    context.getPrefixes(false),
                    new JsonLdOptions());
            return JsonUtils.toPrettyString(compact);
        } catch (Exception e) {
            throw new IOException("JSON-LD could not be generated", e);
        }
    }

    /**
     * Write the current context as a JSON-LD file.
     *
     * @param path the path to write the context
     * @throws IOException on any error
     */
    public void saveContext(String path) throws IOException {
        saveContext(new File(path));
    }

    /**
     * Write the current context as a JSON-LD file.
     *
     * @param file the file to write the context
     * @throws IOException on any error
     */
    public void saveContext(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(getContextString());
        }
    }

}
