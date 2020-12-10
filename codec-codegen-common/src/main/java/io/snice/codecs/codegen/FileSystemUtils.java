package io.snice.codecs.codegen;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.util.HashMap;

public class FileSystemUtils {

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
}
