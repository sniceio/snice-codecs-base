package io.snice.codecs.codegen.common;

import io.snice.codecs.codegen.FileSystemUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The issue with the E212 format is that it is not possible to know by just parsing the data
 * whether or not the MNC is two or three digits. As such, the "best" way is simply to list out
 * all known two digit MNCs and if we have a match, we'll just assume this is indeed a two
 * digit MNC.
 * <p>
 * The below strategy is taken from Wireshark and {@link PacketE212Parser} is indeed parsing the
 * <code>packet-e212.c</code> source file from the Wireshark project. The reason being is that
 * there is a large community that ensures that this file is up to date by monitoring the ITU bulletins.
 * <p>
 * Please see Wireshark https://github.com/wireshark/wireshark.
 */
public class PacketE212Parser {

    public static final String DEFAULT_E212_SOURCE_CODE_FILENAME = "packet-e212.c";

    public static final String E212_CODES_DECLARATION_START = "static const value_string E212_codes[]";
    public static final String MCC_MNC_2DIGITS_CODES_START = "static const value_string mcc_mnc_2digits_codes[]";
    public static final String MCC_MNC_3DIGITS_CODES_START = "static const value_string mcc_mnc_3digits_codes[]";

    private final static Pattern pattern = Pattern.compile(".*?(\\d{3,}).*\"{1}(.*)\"{1}.*");

    /**
     * Find the given startLine and return the index of that line.
     *
     * @param index the index to start the search
     * @param lines the list of lines to search in
     * @param line  the line we are looking for
     * @return the index of the line or empty
     */
    public static Optional<Integer> seek(final int index, final List<String> lines, final String line) {
        for (int i = index; i < lines.size(); ++i) {
            if (lines.get(i).contains(line)) {
                return Optional.of(i);
            }
        }

        return Optional.empty();
    }

    /**
     * Frame out a given section, which assumed to be in a code block starting with the given line.
     *
     * @param source       the lines representing the source file
     * @param sectionStart the start line, which we'll seek to find and we will assume that is
     *                     the start of the code block to frame.
     * @return a list of the lines within the given code block. The start and end of the block will NOT be included.
     * @throws IllegalArgumentException (perhaps should be a parse exception but whatever, this is just codegen stuff... )
     */
    public static Map<Integer, String> frameSection(final List<String> source, final String sectionStart) throws IllegalArgumentException {
        // note: current implementation is very naive and not really a true framer (well).
        // We'll just assume the format of the source file isn't changed and that the end of the block
        // has a single "};" on that line. If Wireshark changes the style, we'll just have to adapt and perhaps
        // then actually do a proper framer...
        final int index = seek(0, source, sectionStart)
                .orElseThrow(() -> new IllegalArgumentException("Unable to locate the start line " + sectionStart));

        final var section = new HashMap<Integer, String>();
        for (int i = index + 1; i < source.size(); ++i) {
            final var currentLine = source.get(i);
            if (currentLine.strip().equals("};")) {
                return section;
            } else {
                processEntry(currentLine).ifPresent(entry -> section.put(entry.getKey(), entry.getValue()));
            }
        }

        throw new IllegalArgumentException("Unable to find the end of the code block for the start line " + section);
    }

    private static Optional<Map.Entry<Integer, String>> processEntry(final String entry) {
        final var matcher = pattern.matcher(entry);
        if (matcher.matches()) {
            final var key = matcher.group(1);
            final var v = matcher.group(2);
            return Optional.of(Map.entry(Integer.parseInt(key), v));
        }

        // Some entries that we don't care about
        if (entry.contains("NULL")) {
            return Optional.empty();
        }

        throw new IllegalArgumentException("Unable to parse " + entry);
    }

    public static List<String> loadResource(final ClassLoader classLoader, final String resource) throws URISyntaxException, IOException {
        final var uri = classLoader.getResource(resource).toURI();
        FileSystemUtils.ensureFileSystem(uri);
        return Files.readAllLines(Path.of(uri));
    }
}
