package io.snice.codecs.codegen.common;

import io.snice.codecs.codegen.FileSystemUtils;
import io.snice.codecs.codegen.common.templates.E212Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class CodeGen {
    private static final Logger logger = LoggerFactory.getLogger(CodeGen.class);
    private static final String CODEC_COMMON_PACKAGE_NAME = "io.snice.codecs.codec";

    public void execute(final Path outputDirectory) throws Exception {
        final var sourceFile = PacketE212Parser.DEFAULT_E212_SOURCE_CODE_FILENAME;
        final var source = PacketE212Parser.loadResource(getClass().getClassLoader(), sourceFile);

        final var e212Mcc = PacketE212Parser.frameSection(source, PacketE212Parser.E212_CODES_DECLARATION_START);
        e212Mcc.remove(1665); // some odd stuff. Just remove it.

        final var e212MccMnc2Digits = PacketE212Parser.frameSection(source, PacketE212Parser.MCC_MNC_2DIGITS_CODES_START);
        final var e212MccMnc3Digits = PacketE212Parser.frameSection(source, PacketE212Parser.MCC_MNC_3DIGITS_CODES_START);

        final var template = E212Template.load();
        final var rendered = template.render(e212Mcc, e212MccMnc2Digits, e212MccMnc3Digits);
        FileSystemUtils.save(outputDirectory, CODEC_COMMON_PACKAGE_NAME, "E212", rendered);
    }

}
