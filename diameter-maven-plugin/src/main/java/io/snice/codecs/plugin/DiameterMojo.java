package io.snice.codecs.plugin;

import io.snice.codecs.codegen.diameter.CodeGen;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jonas@jonasborjesson.com
 */
@Mojo(
        name = "diameter",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true)
public class DiameterMojo extends AbstractMojo {

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    /**
     * Specify output directory where the Java files are generated.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/codec-diameter")
    private File outputDirectory;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final var logger = getLog();
        logger.info("Generating Diameter Code apa2");
        prepareOutputDirectory(outputDirectory);

        try {
            final var uri = getClass().getClassLoader().getResource("codegen.yml").toURI();
            logger.info("Found here: " + uri);
            final var fs = ensureFileSystem(uri);
            final var path = Path.of(uri);
            logger.info("Path: " + path);
            final var is = Files.newInputStream(path);
            logger.info("IS: " + is);

            final var conf = CodeGen.loadConfig(is).copy();
            conf.withWiresharkDictionaryDir(Path.of("/home/jonas/development/3rd-party/wireshark"));
            conf.withSourceRoot(outputDirectory.toPath());
            // final var argparse = CodeGen.parse(path, "--wireshark", "/home/jonas/development/3rd-party/wireshark/diameter");
            final var codegen = CodeGen.of(conf.build());
            codegen.execute();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        // final var defaultConfigFile = CodeGen.locateDefaultConfigFile();
        // logger.info("Looking for default configuration file");
        // logger.info(defaultConfigFile.toString());
    }

    public static FileSystem ensureFileSystem(final URI uri) {
        try {
            if ("jar".equals(uri.getScheme())) {
                final var env = new HashMap<String, Object>();
                env.put("create", "true");
                return FileSystems.newFileSystem(uri, env);
            }
        } catch(final FileSystemAlreadyExistsException | IOException e) {
            // ignore
        }

        return FileSystems.getDefault();

    }

    /**
     * Ensure that the source output directory is valid, if not, complain and bail.
     */
    private static void prepareOutputDirectory(final File outputDirectory) {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        if (!outputDirectory.exists()) {
            throw new IllegalArgumentException("The given output directory " + outputDirectory
                    + " is not valid. Cannot generate the sources here");

        }

    }
}
