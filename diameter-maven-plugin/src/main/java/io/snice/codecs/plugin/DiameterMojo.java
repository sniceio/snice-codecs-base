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
import java.nio.file.Files;
import java.nio.file.Path;

import static io.snice.codecs.codegen.FileSystemUtils.ensureFileSystem;

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
        logger.info("Generating Diameter Code");
        prepareOutputDirectory(outputDirectory);

        try {
            final var uri = getClass().getClassLoader().getResource("codegen.yml").toURI();
            ensureFileSystem(uri);
            final var path = Path.of(uri);
            final var is = Files.newInputStream(path);

            final var diameterXml = getClass().getClassLoader().getResource(CodeGen.DICTIONARY_DIR + File.separator + CodeGen.DICTIONARY_FILE_NAME).toURI();

            final var conf = CodeGen.loadConfig(is).copy();
            conf.withWiresharkDictionaryXml(diameterXml);
            conf.withSourceRoot(outputDirectory.toPath());
            final var codegen = CodeGen.of(conf.build());
            codegen.execute();
        } catch (final Exception e) {
            throw new MojoExecutionException("Unable to generate the Diamter classes due to exception", e);
        }
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
