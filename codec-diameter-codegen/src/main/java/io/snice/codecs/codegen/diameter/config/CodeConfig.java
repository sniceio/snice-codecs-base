package io.snice.codecs.codegen.diameter.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.snice.codecs.codec.diameter.avp.Avp;
import io.snice.codecs.codec.diameter.avp.Vendor;
import io.snice.codecs.codec.diameter.avp.type.DiameterType;
import io.snice.codecs.codegen.diameter.Typedef;
import io.snice.codecs.codegen.diameter.primitives.AvpPrimitive;
import io.snice.codecs.codegen.diameter.primitives.EnumPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.snice.preconditions.PreConditions.assertArgument;

/**
 * Contains configuration for where to render the code, the default package, how to
 * convert names from the dictionary files into Java class names and more.
 */
@JsonDeserialize(builder = CodeConfig.Builder.class)
public class CodeConfig {

    private static final Logger logger = LoggerFactory.getLogger(CodeConfig.class);

    /**
     * This is the validated root directory of pkts.io
     */
    private final Path pktsIoRootDir;

    private final Settings avpSettings;
    private final Settings cmdSettings;
    private final Settings appSettings;


    public static Builder of() {
        return new Builder();
    }

    private CodeConfig(final Path pktsIoRootDir,
                       final Settings avpSettings,
                       final Settings cmdSettings,
                       final Settings appSettings) {
        this.pktsIoRootDir = pktsIoRootDir;
        this.avpSettings = avpSettings;
        this.cmdSettings = cmdSettings;
        this.appSettings = appSettings;
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
        final Class<? extends DiameterType> typeInterface =
                typedef.getImplementingInterface().orElseThrow(() -> new IllegalArgumentException("Unable to render AVP " + avp.getName()
                        + " because missing interface definition for the type " + typedef.getName()));

        final Class<? extends Avp> typeClass =
                typedef.getImplementingClass().orElseThrow(IllegalArgumentException::new);

        avpTypeAttributes.put("class", typeClass.getSimpleName());
        avpTypeAttributes.put("interface", typeInterface.getSimpleName());

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

        imports.add(typeClass.getName());
        imports.add(typeInterface.getName());

        return new Attributes(className, avpSettings.getPackageName(), attributes);
    }

    public Settings getAvpSettings() {
        return avpSettings;
    }

    /**
     * Settings for what to generate, all, some etc.
     */
    public static class GenerationConfig {

        private final List<String> include;
        private final List<String> exclude;

        @JsonCreator
        public GenerationConfig() {
            this(null, null);
        }

        @JsonCreator
        public GenerationConfig(@JsonProperty("include") final List<String> include,
                                @JsonProperty("exclude") final List<String> exclude) {
            this.include = include != null ? include : Collections.emptyList();
            this.exclude = exclude != null ? exclude : Collections.emptyList();
        }

        public boolean generateAll() {
            return include.isEmpty();
        }

        public boolean isExcluded(final String name) {
            return exclude.contains(name);
        }

        public boolean isIncluded(final String name) {
            return include.contains(name);
        }
    }

    public static class PackageConfig {

        private final String main;
        private final String avp;
        private final String cmd;
        private final String app;

        @JsonCreator
        public PackageConfig(@JsonProperty("main") final String main,
                             @JsonProperty("avp") final String avp,
                             @JsonProperty("cmd") final String cmd,
                             @JsonProperty("app") final String app) {
            this.main = main;
            this.avp = append(main, avp);
            this.cmd = append(main, cmd);
            this.app = append(main, app);
        }

        public String getAvpPackage() {
            return avp;
        }

        public String getCmdPackage() {
            return cmd;
        }

        public String getAppPackage() {
            return app;
        }

        private static String append(final String root, final String sub) {
            final String parent = root.endsWith(".") ? root.substring(0, root.length() - 1) : root;
            return sub.startsWith(".") ? parent + sub : parent + "." + sub;
        }
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    public static class Builder {

        /**
         * All the expected directories to which we will generate the code into.
         */
        // private static final String AVP_MVN_DIR = "diameter-test";
        // private static final String CMD_MVN_DIR = "diameter-test";
        // private static final String APP_MVN_DIR = "diameter-test";

        private static final String AVP_MVN_DIR = "codec-diameter";
        private static final String CMD_MVN_DIR = "codec-diameter";
        private static final String APP_MVN_DIR = "codec-diameter";
        /**
         * The directory we expect the diameter code gen code to be located in.
         * We'll use this when trying to automatically determine the pkts.io
         * root dir.
         */
        private static final String CODE_GEN_MVN_DIR = "codec-diameter-codegen";

        /**
         * Default package names
         */
        private static final String DEFAULT_BASE_PACKAGE = "io.pkts.diameter";
        private static final String DEFAULT_AVP_PACKAGE = "avp.api";
        private static final String DEFAULT_CMD_PACKAGE = "cmd";
        private static final String DEFAULT_APP_PACKAGE = "app";

        private final ClassNameConverter classNameConverter = ClassNameConverter.defaultConverter();

        /**
         * The pkts.io root directory.
         */
        private Path projectRoot;

        /**
         * The diameter sub-directories.
         */
        private final Map<String, Path> subdirs = new HashMap<>();

        private PackageConfig packageConfig =
                new PackageConfig(DEFAULT_BASE_PACKAGE, DEFAULT_AVP_PACKAGE, DEFAULT_CMD_PACKAGE, DEFAULT_APP_PACKAGE);

        /**
         * Controls which AVPs should be generated. All? None? Some? All but exclude?
         */
        private GenerationConfig avpGenerationConfig = new GenerationConfig();

        private final GenerationConfig cmdGenerationConfig = new GenerationConfig();

        private final GenerationConfig appGenerationConfig = new GenerationConfig();

        private Builder() {
            // left empty intentionally
        }

        /**
         * Specify the root directory of pkts.io. By default, if you run the codegen from the
         * project itself you do not need to do so since it will figure it out.
         * You really only need to specify it if you run the codegen as a standalone program.
         *
         * @param dir
         * @return
         */
        public Builder withPktsIoRootDir(final Path dir) throws IllegalArgumentException {
            ensureDirectory(dir);
            Stream.of(APP_MVN_DIR, CMD_MVN_DIR, AVP_MVN_DIR).forEach(sub -> {
                subdirs.put(sub, ensureMavenDirectory(dir.resolve(sub)));
            });

            return this;
        }

        @JsonProperty("root")
        public Builder withPktsIoRootDir(final String dir) throws IllegalArgumentException {
            return withPktsIoRootDir(Paths.get(dir));
        }

        @JsonProperty("package")
        public Builder withPackageConfig(final PackageConfig config) {
            this.packageConfig = config;
            return this;
        }

        @JsonProperty("avp")
        public Builder withAvpGenerationConfig(final GenerationConfig config) {
            this.avpGenerationConfig = config;
            return this;
        }

        public CodeConfig build() {
            if (projectRoot == null) {
                final Path path = locatePktsDirectory();
                System.err.println("Parent directory: " + path);
                withPktsIoRootDir(path);
            }

            final Settings avp = new Settings("AVP", subdirs.get(AVP_MVN_DIR), classNameConverter, packageConfig.getAvpPackage(), avpGenerationConfig);
            final Settings cmd = new Settings("CMD", subdirs.get(CMD_MVN_DIR), classNameConverter, packageConfig.getCmdPackage(), cmdGenerationConfig);
            final Settings app = new Settings("APP", subdirs.get(APP_MVN_DIR), classNameConverter, packageConfig.getAppPackage(), appGenerationConfig);

            return new CodeConfig(projectRoot, avp, cmd, app);
        }

        private static void ensureDirectory(final Path dir) {
            assertArgument(Files.isDirectory(dir, LinkOption.NOFOLLOW_LINKS),
                    "The specified directory doesn't exist or is not a directory (" + dir + ")");
        }

        /**
         * Check so that this is indeed a maven directory by checking the layout etc.
         *
         * @param dir
         * @return return the same directory again if it indeed is maven directory.
         * This is just a way to write more fluent APIs...
         */
        private static Path ensureMavenDirectory(final Path dir) {
            ensureDirectory(dir);
            assertArgument(Files.isRegularFile(dir.resolve("pom.xml")), "Unable to find the pom.xml for directory " + dir);
            return dir;
        }

        /**
         * If the user doesn't specify the networking.snice.io root directory we'll try and figure it out by using the DUMMY
         * file that we know is in the codegen directory and then we'll work our way up from there.
         *
         * @return
         * @throws IllegalArgumentException
         */
        private static Path locatePktsDirectory() throws IllegalArgumentException {
            try {
                final Path dummy = Paths.get(CodeConfig.class.getResource("DUMMY").toURI());
                final Path path = cdToParent(dummy, CODE_GEN_MVN_DIR)
                        .orElseThrow(() -> new IllegalArgumentException("Unable to locate the parent directory " + CODE_GEN_MVN_DIR));

                return path.getParent();
            } catch (final URISyntaxException e) {
                logger.warn("Unable to locate the DUMMY file due to URI exception", e);
                throw new IllegalArgumentException("Unable to locate the pkts.io main directory " +
                        "because I couldn't locate the DUMMY file");
            }
        }

        /**
         * Find the parent directory.
         *
         * @param path our current path object
         * @param dir  the parent directory we are looking for.
         * @return the directory or if not found, an empty optional.
         */
        private static Optional<Path> cdToParent(final Path path, final String dir) {
            if (path == null) {
                return Optional.empty();
            }

            final Path current = path.getFileName();
            if (dir.equals(current.toString())) {
                return Optional.of(path);
            }

            return cdToParent(path.getParent(), dir);
        }


    }


}
