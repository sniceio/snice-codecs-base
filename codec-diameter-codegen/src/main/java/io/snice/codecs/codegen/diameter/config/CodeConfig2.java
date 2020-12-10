package io.snice.codecs.codegen.diameter.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.snice.codecs.codegen.ClassNameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Path;
import java.util.*;

/**
 * Contains configuration for where to render the code, the default package, how to
 * convert names from the dictionary files into Java class names and more.
 */
@JsonDeserialize(builder = CodeConfig2.Builder.class)
public class CodeConfig2 {

    private static final Logger logger = LoggerFactory.getLogger(CodeConfig2.class);

    private final PackageConfig packageConfig;
    private final Optional<Path> sourceRoot;
    private final URI dictionaryXml;

    private final Optional<GenerationConfig> avpPackageConfig;
    private final Optional<GenerationConfig> cmdPackageConfig;
    private final Optional<GenerationConfig> appPackageConfig;


    public static Builder of() {
        return new Builder();
    }

    private CodeConfig2(final PackageConfig packageConfig,
                        final URI dictionaryXml,
                        final Optional<Path> sourceRoot,
                        final Optional<GenerationConfig> avpPackageConfig,
                        final Optional<GenerationConfig> cmdPackageConfig,
                        final Optional<GenerationConfig> appPackageConfig) {
        this.packageConfig = packageConfig;
        this.dictionaryXml = dictionaryXml;
        this.sourceRoot = sourceRoot;
        this.avpPackageConfig = avpPackageConfig;
        this.cmdPackageConfig = cmdPackageConfig;
        this.appPackageConfig = appPackageConfig;
    }

    public URI getDictionaryXml() {
        return dictionaryXml;
    }

    public Optional<GenerationConfig> getAvpPackageConfig() {
        return avpPackageConfig;
    }

    public Optional<GenerationConfig> getCmdPackageConfig() {
        return cmdPackageConfig;
    }

    public Optional<GenerationConfig> getAppPackageConfig() {
        return appPackageConfig;
    }

    public Optional<Path> getSourceRootDir() {
        return sourceRoot;
    }

    /*
    public Optional<Path> getWiresharkDictionaryRoot() {
        return wiresharkDictionaryRoot;
    }
     */

    public PackageConfig getPackageConfig() {
        return packageConfig;
    }

    public Builder copy() {
        final var builder = new Builder();
        builder.withWiresharkDictionaryXml(dictionaryXml);
        sourceRoot.ifPresent(builder::withSourceRoot);
        appPackageConfig.ifPresent(builder::withAppGenerationConfig);
        cmdPackageConfig.ifPresent(builder::withCmdGenerationConfig);
        avpPackageConfig.ifPresent(builder::withAvpGenerationConfig);
        builder.withPackageConfig(packageConfig);

        return builder;
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
         *
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
         * The root directory where we'll generate all the source files
         */
        private Path sourceRoot;

        private URI dictionaryXml;

        private URI dictionaryDtd;

        /**
         * The diameter sub-directories.
         */
        private final Map<String, Path> subdirs = new HashMap<>();

        private PackageConfig packageConfig =
                new PackageConfig(DEFAULT_BASE_PACKAGE, DEFAULT_AVP_PACKAGE, DEFAULT_CMD_PACKAGE, DEFAULT_APP_PACKAGE);

        /**
         * Controls which AVPs should be generated. All? None? Some? All but exclude?
         */
        private Optional<GenerationConfig> avpGenerationConfig = Optional.empty();

        private Optional<GenerationConfig> cmdGenerationConfig = Optional.empty();

        private Optional<GenerationConfig> appGenerationConfig = Optional.empty();

        private Builder() {
            // left empty intentionally
        }

        @JsonProperty("sourceRoot")
        public Builder withSourceRoot(final Path root) {
            this.sourceRoot = root;
            return this;
        }

        @JsonProperty("package")
        public Builder withPackageConfig(final PackageConfig config) {
            this.packageConfig = config;
            return this;
        }

        public Builder withWiresharkDictionaryXml(final URI xml) {
            this.dictionaryXml = xml;
            return this;
        }

        @JsonProperty("avp")
        public Builder withAvpGenerationConfig(final GenerationConfig config) {
            this.avpGenerationConfig = Optional.ofNullable(config);
            return this;
        }

        @JsonProperty("cmd")
        public Builder withCmdGenerationConfig(final GenerationConfig config) {
            this.cmdGenerationConfig = Optional.ofNullable(config);
            return this;
        }

        @JsonProperty("app")
        public Builder withAppGenerationConfig(final GenerationConfig config) {
            this.appGenerationConfig = Optional.ofNullable(config);
            return this;
        }

        public CodeConfig2 build() {
            return new CodeConfig2(packageConfig, dictionaryXml, Optional.ofNullable(sourceRoot),
                    avpGenerationConfig, cmdGenerationConfig, appGenerationConfig);
        }

    }


}
