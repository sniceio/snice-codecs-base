package io.snice.codecs.codegen.gtp;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.templates.*;
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

    private static final String GTPC_V1_PACKAGE_NAME = "io.snice.codecs.codec.gtp.gtpc.v1";

    private static final String GTPC_V2_PACKAGE_NAME = "io.snice.codecs.codec.gtp.gtpc.v2";
    private static final String GTPC_V2_TLIV_PACKAGE_NAME = GTPC_V2_PACKAGE_NAME + ".tliv";

    private static final Logger logger = LoggerFactory.getLogger(CodeGen.class);

    public static List<Gtpv2InfoElementMetaData> loadInfoElementMetaData() throws Exception {
        return loadSpec(Gtpv2InfoElementMetaData.class, "specifications/info_elements.yml");
    }

    public static List<Gtpv2MessageTypeMetaData> loadMessageTypeMetaData() throws Exception {
        return loadSpec(Gtpv2MessageTypeMetaData.class, "specifications/message_types.yml");
    }

    public static List<Gtpv1MessageTypeMetaData> loadGtpv1MessageTypeMetaData() throws Exception {
        return loadSpec(Gtpv1MessageTypeMetaData.class, "specifications/gtpv1_message_types.yml");
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
        mapper.registerModule(new Jdk8Module());

        final var list = new ArrayList<C>();
        final MappingIterator<C> it = mapper.readerFor(clz).readValues(getURL(file));
        it.forEachRemaining(list::add);
        return Collections.unmodifiableList(list);
    }

    public static void execute(final Path outputDirectory) throws Exception {
        final ClassNameConverter classNameConverter = ClassNameConverter.defaultConverter();
        final var infoElements = loadInfoElementMetaData();

        final String ies = InfoElementTemplate.load().render(infoElements);
        save(outputDirectory, GTPC_V2_PACKAGE_NAME, "Gtp2InfoElement", ies);

        final var tlivTemplate = TlivTemplate.load();
        infoElements.forEach(ie -> {
            final var tliv = tlivTemplate.render(classNameConverter, ie);
            final var className = classNameConverter.convert(ie.getEnumValue());
            save(outputDirectory, GTPC_V2_TLIV_PACKAGE_NAME, className, tliv);
        });

        final String framer = TlivFramerTemplate.load().render(classNameConverter, infoElements);
        save(outputDirectory, GTPC_V2_TLIV_PACKAGE_NAME, "TlivFramer", framer);

        final String messagesTypes = MessageTypeTemplate.load().render(loadMessageTypeMetaData());
        save(outputDirectory, GTPC_V2_PACKAGE_NAME, "Gtp2MessageType", messagesTypes);

        final String gtpv1MessagesTypes = Gtpv1MessageTypeTemplate.load().render(loadGtpv1MessageTypeMetaData());
        save(outputDirectory, GTPC_V1_PACKAGE_NAME, "Gtp1MessageType", gtpv1MessagesTypes);

    }

    private static void save(final Path outpuDirectory, final String javaPackageName, final String javaName, final String content) {
        final Path packageDir = outpuDirectory.resolve(javaPackageName.replaceAll("\\.", File.separator));
        final Path fullFileName = packageDir.resolve(javaName + ".java");
        logger.debug("Saving {} as {}", javaName, fullFileName);
        try {
            Files.createDirectories(packageDir);
            Files.write(fullFileName, content.getBytes());
        } catch (final IOException e) {
            throw new RuntimeException("Unable to save " + fullFileName + " to " + packageDir, e);
        }
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
