package io.snice.codecs.codegen.gtp;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.snice.codecs.codec.gtp.v1.common.Format;
import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.templates.Gtpv1InformationElementsTemplate;
import io.snice.codecs.codegen.gtp.templates.Gtpv1MessageTypeTemplate;
import io.snice.codecs.codegen.gtp.templates.InfoElementTemplate;
import io.snice.codecs.codegen.gtp.templates.MessageTypeTemplate;
import io.snice.codecs.codegen.gtp.templates.TlivFramerTemplate;
import io.snice.codecs.codegen.gtp.templates.TlivTemplate;
import io.snice.codecs.codegen.gtp.templates.TlvFramerTemplate;
import io.snice.codecs.codegen.gtp.templates.TlvTemplate;
import io.snice.codecs.codegen.gtp.templates.TvFramerTemplate;
import io.snice.codecs.codegen.gtp.templates.TvTemplate;
import liqp.RenderSettings;
import liqp.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.snice.codecs.codegen.FileSystemUtils.getURL;
import static io.snice.codecs.codegen.FileSystemUtils.save;

public class CodeGen {

    private static final String GTPC_V1_PACKAGE_NAME = "io.snice.codecs.codec.gtp.gtpc.v1";
    private static final String GTPC_V1_TV_PACKAGE_NAME = GTPC_V1_PACKAGE_NAME + ".ie.tv";
    private static final String GTPC_V1_TLV_PACKAGE_NAME = GTPC_V1_PACKAGE_NAME + ".ie.tlv";

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

    public static List<Gtpv1InfoElementMetaData> loadGtpv1InfoElementMetaData() throws Exception {
        return loadSpec(Gtpv1InfoElementMetaData.class, "specifications/gtpv1_information_elements.yml");
    }

    public static Template loadTemplate(final String file) throws Exception {
        final var url = getURL(file);
        final var p = Paths.get(url.toURI());
        final var liquid = Files.readString(p);
        final RenderSettings settings = new RenderSettings.Builder().withStrictVariables(false).build();
        return Template.parse(liquid).withRenderSettings(settings);
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

        final var tvs = loadGtpv1InfoElementMetaData();
        final String gtpv1InfoElements = Gtpv1InformationElementsTemplate.load().render(tvs);
        save(outputDirectory, GTPC_V1_PACKAGE_NAME, "Gtp1InfoElement", gtpv1InfoElements);

        // Render Type Value Information elements and the corresponding framer
        final var tvTemplate = TvTemplate.load();
        tvs.stream().filter(tv -> tv.getFormat() == Format.TV).forEach(tv -> {
            final var result = tvTemplate.render(classNameConverter, GTPC_V1_TV_PACKAGE_NAME, tv);
            save(outputDirectory, GTPC_V1_TV_PACKAGE_NAME, result.getJavaClassName(), result.getResult());
        });

        final String typeValueFramer = TvFramerTemplate.load().render(classNameConverter, GTPC_V1_TV_PACKAGE_NAME, tvs);
        save(outputDirectory, GTPC_V1_TV_PACKAGE_NAME, "TypeValueFramer", typeValueFramer);

        // Render Type Length Value Information elements and the corresponding framer
        final var tlvTemplate = TlvTemplate.load();
        tvs.stream().filter(tv -> tv.getFormat() == Format.TLV).forEach(tv -> {
            final var result = tlvTemplate.render(classNameConverter, GTPC_V1_TLV_PACKAGE_NAME, tv);
            save(outputDirectory, GTPC_V1_TLV_PACKAGE_NAME, result.getJavaClassName(), result.getResult());
        });

        final String typeLengthValueFramer = TlvFramerTemplate.load().render(classNameConverter, GTPC_V1_TLV_PACKAGE_NAME, tvs);
        save(outputDirectory, GTPC_V1_TLV_PACKAGE_NAME, "TypeLengthValueFramer", typeLengthValueFramer);
    }


}
