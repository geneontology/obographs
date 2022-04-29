package org.geneontology.obographs.cli.commands;

import picocli.CommandLine;
import picocli.CommandLine.IVersionProvider;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * {@link IVersionProvider} implementation that returns version information from the obographs-cli-x.x.jar file's {@code /META-INF/MANIFEST.MF} file.
 * Requires building with maven-jar-plugin using the manifest.addDefaultImplementationEntries=true option.
 *
 * Adapted from https://github.com/remkop/picocli/blob/main/picocli-examples/src/main/java/picocli/examples/VersionProviderDemo2.java.
 */
public class ManifestVersionProvider implements IVersionProvider {

    private static final String MAVEN_ARTIFACT_ID = "obographs-cli";

    public String[] getVersion() throws Exception {
        Enumeration<URL> resources = CommandLine.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            try {
                Manifest manifest = new Manifest(url.openStream());
                // This is the Maven ${project.name} generated from the artifactId in the project pom.xml
                if (isApplicableManifest(manifest, MAVEN_ARTIFACT_ID)) {
                    Attributes attr = manifest.getMainAttributes();
                    var version = (String) get(attr, "Implementation-Version");
                    return new String[]{MAVEN_ARTIFACT_ID + " version \"" + version + "\""};
                }
            } catch (IOException ex) {
                return new String[]{"Unable to read from " + url + ": " + ex};
            }
        }
        return new String[0];
    }

    private boolean isApplicableManifest(Manifest manifest, String mavenArtifactId) {
        Attributes attributes = manifest.getMainAttributes();
        return mavenArtifactId.equals(get(attributes, "Implementation-Title"));
    }

    private static Object get(Attributes attributes, String key) {
        return attributes.get(new Attributes.Name(key));
    }
}
