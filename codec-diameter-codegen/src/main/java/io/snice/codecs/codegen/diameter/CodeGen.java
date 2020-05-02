package io.snice.codecs.codegen.diameter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.snice.codecs.codec.diameter.avp.Vendor;
import io.snice.codecs.codegen.diameter.config.Attributes;
import io.snice.codecs.codegen.diameter.config.ClassNameConverter;
import io.snice.codecs.codegen.diameter.config.CodeConfig2;
import io.snice.codecs.codegen.diameter.config.Settings;
import io.snice.codecs.codegen.diameter.primitives.AvpPrimitive;
import io.snice.codecs.codegen.diameter.primitives.EnumPrimitive;
import io.snice.codecs.codegen.diameter.templates.AvpFramerTemplate;
import io.snice.codecs.codegen.diameter.templates.AvpTemplate;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.snice.preconditions.PreConditions.assertArgument;

public class CodeGen {

    private static final Logger logger = LoggerFactory.getLogger(CodeGen.class);

    private static final String DICTIONARY_FILE_NAME = "dictionary.xml";
    private static final String DICTIONARY_DTD_FILE_NAME = "dictionary.dtd";

    private final CodeConfig2 config;
    private final Settings avpSettings;
    private final DiameterCollector collector = new DiameterCollector();

    public static CodeGen of(final CodeConfig2 config) throws IllegalArgumentException {

        final var sourceRoot = config.getSourceRootDir()
                .orElseThrow(() -> new IllegalArgumentException("You must specify the root directory to where " +
                        "all source code will be generated"));

        final var dictionaryDir = ensureDictionaryDirectory(config.getWiresharkDictionaryRoot());

        final var actualConfig = config.copy()
                .withSourceRoot(sourceRoot)
                .withWiresharkDictionaryDir(dictionaryDir)
                .build();

        final ClassNameConverter classNameConverter = ClassNameConverter.defaultConverter();

        final Settings avp = new Settings("AVP", sourceRoot.toAbsolutePath(), classNameConverter, actualConfig.getPackageConfig().getAvpPackage(), actualConfig.getAvpPackageConfig().get());
        return new CodeGen(actualConfig, avp);
    }

    private CodeGen(final CodeConfig2 config, final Settings avpSettings) {
        this.config = config;
        this.avpSettings = avpSettings;
    }

    public void execute() {
        try {
            final Path dictionary = config.getWiresharkDictionaryRoot().get().resolve(DICTIONARY_FILE_NAME);
            logger.info("Parsing " + dictionary);
            final WiresharkDictionaryReader reader = new WiresharkDictionaryReader(collector);
            reader.parse(dictionary);
            final var renderedAvps = renderAvps();
            renderAvpFramer(renderedAvps);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private List<Attributes> renderAvps() throws IOException, URISyntaxException {
        final Settings settings = avpSettings;
        final List<AvpPrimitive> avps;
        if (settings.renderAll()) {
            logger.info("Rendering all found AVPs, minus the ones on the exclude list.");
            avps = collector.getAvps().stream().filter(avp -> !settings.isExcluded(avp.getName())).collect(Collectors.toList());
        } else {
            logger.info("Rendering only those AVPs explicitly listed on the \"include\" list");
            avps = collector.getAvps().stream().filter(avp -> settings.isIncluded(avp.getName())).collect(Collectors.toList());
        }

        final List<Attributes> renderedAvps = new ArrayList<>();
        for (final AvpPrimitive avp : avps) {
            final AvpTemplate template = AvpTemplate.load(avp);
            final Attributes attributes = createAvpConfig(avp);
            renderedAvps.add(attributes);

            final String rendered = template.render(attributes.getAttributes());
            save(settings, attributes, rendered);
        }

        return renderedAvps;
    }

    /**
     * Based on the AVPs that we rendered, generate a framer for those.
     */
    private void renderAvpFramer(final List<Attributes> avps) throws IOException, URISyntaxException {
        final AvpFramerTemplate template = AvpFramerTemplate.load();

        // TODO: should be configurable
        Attributes framerAttributes = new Attributes("AvpFramer", "io.snice.codecs.codec.diameter.avp", Map.of());
        final String rendered = template.render(framerAttributes, avps);
        save(avpSettings, framerAttributes, rendered);
    }

    public Attributes createAvpConfig(final AvpPrimitive avp) {

        // annoying!
        final Map<String, Object> attributes = new HashMap<>();
        final Map<String, Object> javaAttributes = new HashMap<>();
        final Map<String, Object> javaClassAttributes = new HashMap<>();
        final Map<String, Object> avpAttributes = new HashMap<>();
        final Map<String, Object> avpTypeAttributes = new HashMap<>();

        // Java imports. Just put them here.
        final List<String> imports = new ArrayList<>();

        // build up the hierarchy of attributes.
        attributes.put("avp", avpAttributes);
        attributes.put("java", javaAttributes);
        javaAttributes.put("imports", imports);
        javaAttributes.put("class", javaClassAttributes);

        avpAttributes.put("type", avpTypeAttributes);
        avpAttributes.put("tbcd_formatted", false);

        // The Java interface name of our avp and the package
        final String className = avpSettings.convert(avp.getName());
        javaClassAttributes.put("name", className);
        javaAttributes.put("package", avpSettings.getPackageName());

        if ("Msisdn".equals(className)) {
            avpAttributes.put("tbcd_formatted", true);
        }

        avpAttributes.put("code", avp.getCode());
        avpAttributes.put("mandatory_bit", avp.getMandatoryBit().toString());
        avpAttributes.put("protected_bit", avp.getProtectedBit().toString());
        avpAttributes.put("vendor_bit", avp.getVendorBit().toString());
        avpAttributes.put("may_encrypt_bit", avp.getMayEncryptBit());
        avpAttributes.put("vendor", avp.getVendor().orElse(Vendor.NONE).toString());

        final Typedef typedef = avp.getTypedef();
        final String typeInterface =
                typedef.getImplementingInterface().orElseThrow(() -> new IllegalArgumentException("Unable to render AVP " + avp.getName()
                        + " because missing interface definition for the type " + typedef.getName()));
        final String typeInterfaceFqdn = typedef.getImplementingInterfaceFqdn().orElseThrow(IllegalAccessError::new);

        final String typeClass =
                typedef.getImplementingClass().orElseThrow(IllegalArgumentException::new);
        final String typeClassFqdn =
                typedef.getImplementingClassFqdn().orElseThrow(IllegalArgumentException::new);

        avpTypeAttributes.put("class", typeClass);
        avpTypeAttributes.put("interface", typeInterface);

        // Unfortunately, for ResultCode and ExperimentalResultCode we also have to stick the code
        // value at the end because there are overlapping enum names
        final boolean appendEnumCode = List.of("ResultCode", "ExperimentalResultCode").stream().filter(className::equals).findFirst().isPresent();

        if (avp.isEnumerated()) {
            final List<EnumPrimitive> enums = avp.toEnumerated().getSortedEnums();
            final List<String> enumList = enums.stream().map(e -> {
                // we're building up the enum declaration ourselves here.
                // was easier than messing with the liquid template.
                final String fullName = appendEnumCode ? e.getEnumName() + "_" + e.getEnumCode() : e.getEnumName();
                // return e.getEnumName() + "_" + e.getEnumCode() + "(\"" + e.getEnumName() + "\", " + e.getEnumCode() + ")";
                return fullName + "(\"" + e.getEnumName() + "\", " + e.getEnumCode() + ")";
            }).collect(Collectors.toList());
            avpAttributes.put("enum_definition", enumList);

            // For enums, and the way we have structured Avp<? extends DiameterPrimitive>,
            // it means that we have an actual enum generated but then we also need to follow the AVP<?>
            // structure and an enum cannot be extended in java so we are also creating a wrapper
            // interface. In that case, we still want to actually be able to reference it like an enum
            // with final static single instance variables of a particular "enum", the below does that.
            final List<String> staticVariables = enums.stream().map(e -> {
                final String enumName = avpSettings.convert(e.getEnumName());
                final String variableName = appendEnumCode ? enumName + e.getEnumCode() : enumName;
                return className + " " + variableName + " = " + className + ".of(" + e.getEnumCode() + ");";
            }).collect(Collectors.toList());
            avpAttributes.put("variable_definition", staticVariables);

            final List<String> enumSwitch = enums.stream().map(e -> {
                final String fullName = appendEnumCode ? e.getEnumName() + "_" + e.getEnumCode() : e.getEnumName();
                return "case " + e.getEnumCode() + ": return Optional.of(" + fullName + ");";
            }).collect(Collectors.toList());
            avpAttributes.put("enum_switch", enumSwitch);
        }

        imports.add(typeClassFqdn);
        imports.add(typeInterfaceFqdn);

        return new Attributes(className, avpSettings.getPackageName(), attributes);
    }

    private void save(final Settings settings, final Attributes attributes, final String content) throws IOException {
        final Path src = settings.getJavaSrcDir();
        final Path packageDir = src.resolve(attributes.getPackage().replaceAll("\\.", File.separator));
        final Path fullFileName = packageDir.resolve(attributes.getName() + ".java");
        logger.debug("Saving {} as {}", attributes.getName(), fullFileName);
        Files.createDirectories(packageDir);
        Files.write(fullFileName, content.getBytes());
    }

    public static ArgumentParser configureParser(final Path defaultConfig) {
        final ArgumentParser parser = ArgumentParsers.newFor("codegen").build();
        parser.description("Code generator for diameter");

        // the directory where we expect all the dicionary.xml files to live.
        parser.addArgument("--wireshark")
                .help("Directory where the wireshark diameter dictionary.xml files lives")
                .metavar("<wireshark dir>")
                .required(true);

        // configuration file with all our settings for e.g. where to generate the
        // code, if there are any that should be skipped etc etc.
        parser.addArgument("--config")
                .help("The configuration file")
                .setDefault(defaultConfig.toAbsolutePath().toString())
                .metavar("<config>");

        parser.addArgument("--source-dir")
                .help("The directory to where the source code will be generated")
                .setDefault(".")
                .metavar("<source-dir>");

        return parser;
    }

    /**
     * Ensure that the specified directory has all the various xml files etc.
     *
     * @param dir
     * @return
     * @throws IllegalArgumentException
     */
    private static Path ensureDictionaryDirectory(final Optional<Path> dir) throws IllegalArgumentException {
        final Path root = dir.orElseThrow(() -> new IllegalArgumentException("You must specify the wireshark dictionary directory"));
        assertArgument(Files.exists(root), "The given directory doesn't exist (" + root + ")");
        assertArgument(Files.isDirectory(root), "The given directory is not a directory (" + root + ")");

        final Path altRoot = root.resolve("diameter");

        // if we can't find the dictionary.xml in the given directory, see if it exists in
        // a sub-directory named "diameter" since that is the structure of wireshark.
        for (final Path p : Arrays.asList(root, altRoot)) {
            final Path xml = p.resolve(DICTIONARY_FILE_NAME);
            final Path dtd = p.resolve(DICTIONARY_DTD_FILE_NAME);
            if (Files.exists(xml) && Files.exists(dtd)) {
                return p;
            }
        }

        throw new IllegalArgumentException("Unable to locate the dictionary.xml and " +
                "dictionary.dtd in the given directory. I even checked the subdirectory 'diameter' (" + altRoot + ")");
    }

    public static CodeConfig2 loadConfig(final String config) throws IOException {
        final Path path = Paths.get(config).toAbsolutePath();
        return loadConfig(path);
    }

    public static CodeConfig2 loadConfig(final URI config) throws IOException {
        final Path path = Paths.get(config).toAbsolutePath();
        return loadConfig(path);
    }

    public static CodeConfig2 loadConfig(final Path path) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        assertArgument(Files.exists(path), "The given config file does not exist (" + path + ")");
        assertArgument(Files.isRegularFile(path), "The given config file is not a regular file (" + path + ")");
        return mapper.readValue(path.toFile(), CodeConfig2.class);
    }

    public static CodeConfig2 loadConfig(final InputStream stream) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(stream, CodeConfig2.class);
    }


    /**
     * @return
     * @throws IllegalArgumentException
     */
    public static Path locateDefaultConfigFile() throws IllegalArgumentException {
        try {
            final var defaultConfigFile = "codegen.yml";
            var url = Thread.currentThread().getContextClassLoader().getResource(defaultConfigFile);
            if (url == null) {
                url = CodeGen.class.getResource(defaultConfigFile);
            }

            return Paths.get(url.toURI());

        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Unable to locate the default configuration file for the "
                    + "code generation. Please specify a configuration file on the command line");
        }
    }

    public static Optional<CodeGen> parse(final Path defaultCodeConfigFile, final String... args) {
        final ArgumentParser parser = configureParser(defaultCodeConfigFile);

        try {
            final var result = parser.parseArgs(args);
            final var config = loadConfig(result.getString("config"));
            final var confBuilder = config.copy();

            final var wireshark = result.getString("wireshark");
            if (wireshark != null) {
                confBuilder.withWiresharkDictionaryDir(Paths.get(wireshark));
            }

            confBuilder.withSourceRoot(Paths.get(result.getString("source_dir")));

            return Optional.of(CodeGen.of(confBuilder.build()));

        } catch (final ArgumentParserException e) {
            parser.handleError(e);
        } catch (final IllegalArgumentException e) {
            logger.error("Illegal argument", e);
        } catch (final IOException e) {
            logger.error("Unable to read/write from/to file", e);
        }

        return Optional.empty();
    }

    public static void main(final String... args) throws Exception {
        final Path defaultCodeConfigFile = locateDefaultConfigFile();
        parse(defaultCodeConfigFile, args).ifPresent(CodeGen::execute);
    }
}
