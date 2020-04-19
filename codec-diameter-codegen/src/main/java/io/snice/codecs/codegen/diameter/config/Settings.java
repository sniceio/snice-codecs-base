package io.snice.codecs.codegen.diameter.config;

import java.nio.file.Path;

public class Settings {

    /**
     * The name of the settings, either AVP, CMD or APP.
     */
    private final String name;

    /**
     * The root directory
     */
    private final Path rootDir;

    private final Path srcDir;

    private final Path testDir;

    /**
     * The Java package name.
     */
    private final String packageName;

    private final ClassNameConverter classNameConverter;

    private final CodeConfig.GenerationConfig genConfig;

    public Settings(final String name,
                    final Path rootDir,
                    final ClassNameConverter converter,
                    final String packageName,
                    final CodeConfig.GenerationConfig genConfig) {
        this.name = name;
        this.classNameConverter = converter;
        this.rootDir = rootDir;
        this.srcDir = rootDir.resolve("src/main/java");
        this.testDir = rootDir.resolve("src/test/java");
        this.packageName = packageName;
        this.genConfig = genConfig;
    }

    public boolean renderAll() {
        return genConfig.generateAll();
    }

    public boolean isIncluded(final String name) {
        return genConfig.isIncluded(name);
    }

    public boolean isExcluded(final String name) {
        return genConfig.isExcluded(name);
    }

    public String convert(final String name) {
        return classNameConverter.convert(name);
    }

    public String getPackageName() {
        return packageName;
    }

    public Path getRootDir() {
        return rootDir;
    }

    public Path getJavaSrcDir() {
        return srcDir;
    }
}
