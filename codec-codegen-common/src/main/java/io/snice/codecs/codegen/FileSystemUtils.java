package io.snice.codecs.codegen;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class FileSystemUtils {

    public static URL getURL(final String file) throws Exception {
        final var url = Thread.currentThread().getContextClassLoader().getResource(file);
        ensureFileSystem(url.toURI());
        return url;
    }

    /**
     * Ensure that the source output directory is valid, if not, complain and bail.
     */
    public static void prepareOutputDirectory(final File outputDirectory) {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        if (!outputDirectory.exists()) {
            throw new IllegalArgumentException("The given output directory " + outputDirectory
                    + " is not valid. Cannot generate the sources here");

        }
    }

    public static FileSystem ensureFileSystem(final URI uri) {
        try {
            if ("jar".equals(uri.getScheme())) {
                final var env = new HashMap<String, Object>();
                env.put("create", "true");
                return FileSystems.newFileSystem(uri, env);
            }
        } catch (final FileSystemAlreadyExistsException | IOException e) {
            // ignore
        }

        return FileSystems.getDefault();
    }

    public static void save(final Path outpuDirectory, final String javaPackageName, final String javaName, final String content) {
        final Path packageDir = outpuDirectory.resolve(javaPackageName.replaceAll("\\.", File.separator));
        final Path fullFileName = packageDir.resolve(javaName + ".java");
        try {
            Files.createDirectories(packageDir);
            Files.write(fullFileName, content.getBytes());
        } catch (final IOException e) {
            throw new RuntimeException("Unable to save " + fullFileName + " to " + packageDir, e);
        }
    }
}
