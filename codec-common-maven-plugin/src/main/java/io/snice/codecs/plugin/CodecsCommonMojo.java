package io.snice.codecs.plugin;

import io.snice.codecs.codegen.common.CodeGen;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 *
 * @author jonas@jonasborjesson.com
 */
@Mojo(
        name = "codecs-common",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.COMPILE,
        requiresProject = true)
public class CodecsCommonMojo extends AbstractMojo {

    /**
     * The current Maven project.
     */
    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    /**
     * Specify output directory where the Java files are generated.
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/codec-common")
    private File outputDirectory;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        final var logger = getLog();
        logger.info("Generating Common Codecs Code");
        prepareOutputDirectory(outputDirectory);

        try {
            final var codeGen = new CodeGen();
            codeGen.execute(outputDirectory.toPath());
        } catch (final Exception e) {
            throw new MojoExecutionException("Unable to generate the code", e);
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
