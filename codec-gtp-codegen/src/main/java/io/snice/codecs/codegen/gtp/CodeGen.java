package io.snice.codecs.codegen.gtp;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.snice.codecs.codegen.gtp.templates.InfoElementTemplate;
import io.snice.codecs.codegen.gtp.templates.MessageTypeTemplate;
import liqp.RenderSettings;
import liqp.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CodeGen {

    private static final String GTPC_V2_PACKAGE_NAME = "io.snice.networking.codec.gtp.gtpc.v2";

    private static final Logger logger = LoggerFactory.getLogger(CodeGen.class);

    public static List<InfoElementMetaData> loadInfoElementMetaData() throws Exception {
        return loadSpec(InfoElementMetaData.class, "specifications/info_elements.yml");
    }

    public static List<MessageTypeMetaData> loadMessageTypeMetaData() throws Exception {
        return loadSpec(MessageTypeMetaData.class, "specifications/message_types.yml");
    }

    public static Template loadTemplate(final String file) throws Exception {
        final var url = CodeGen.getURL(file);
        final var p = Paths.get(url.toURI());
        final var liquid = Files.readString(p);
        final RenderSettings settings = new RenderSettings.Builder().withStrictVariables(false).build();
        return Template.parse(liquid).withRenderSettings(settings);
    }

    public static URL getURL(final String file) throws Exception {
        final var url = Thread.currentThread().getContextClassLoader().getResource(file);
        ensureFileSystem(url.toURI());
        return url;
    }

    public static <C> List<C> loadSpec(final Class<C> clz, final String file) throws Exception {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        final SimpleModule module = new SimpleModule();
        mapper.registerModule(module);

        final var list = new ArrayList<C>();
        final MappingIterator<C> it = mapper.readerFor(clz).readValues(getURL(file));
        it.forEachRemaining(list::add);
        return Collections.unmodifiableList(list);
    }

    public static void execute(final Path outputDirectory) throws Exception {
        final String ies = InfoElementTemplate.load().render(loadInfoElementMetaData());
        save(outputDirectory, GTPC_V2_PACKAGE_NAME, "Gtp2InfoElement", ies);

        final String messagesTypes = MessageTypeTemplate.load().render(loadMessageTypeMetaData());
        save(outputDirectory, GTPC_V2_PACKAGE_NAME, "Gtp2MessageType", messagesTypes);
    }

    private static void save(final Path outpuDirectory, String javaPackageName, String javaName, final String content) throws IOException {
        final Path packageDir = outpuDirectory.resolve(javaPackageName.replaceAll("\\.", File.separator));
        final Path fullFileName = packageDir.resolve(javaName + ".java");
        logger.debug("Saving {} as {}", javaName, fullFileName);
        Files.createDirectories(packageDir);
        Files.write(fullFileName, content.getBytes());
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


}
