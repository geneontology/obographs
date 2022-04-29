package org.geneontology.obographs.cli;

import org.geneontology.obographs.cli.commands.Convert;
import org.geneontology.obographs.cli.commands.ManifestVersionProvider;
import org.geneontology.obographs.cli.commands.Validate;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Spec;

@Command(name = "obographs",
        versionProvider = ManifestVersionProvider.class,
        mixinStandardHelpOptions = true,
        description = "OBO Graphs: Developer-friendly graph-oriented ontology in JSON/YAML",
        subcommands = {
                Convert.class,
                Validate.class,
        }
)
public class OboGraphs implements Runnable {

    @Spec
    CommandSpec spec;

    @Override
    public void run() {
        // if the command was invoked without subcommand, show the usage help
        spec.commandLine().usage(System.err);
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new OboGraphs()).execute(args));
    }
}
