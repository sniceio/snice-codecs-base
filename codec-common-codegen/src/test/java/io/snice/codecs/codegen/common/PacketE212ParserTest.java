package io.snice.codecs.codegen.common;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PacketE212ParserTest {

    private List<String> source;

    @Before
    public void setUp() throws Exception {
        source = PacketE212Parser.loadResource(getClass().getClassLoader(), PacketE212Parser.DEFAULT_E212_SOURCE_CODE_FILENAME);
    }

    @Test
    public void testFrameE212() {
        final var section = PacketE212Parser.frameSection(source, PacketE212Parser.E212_CODES_DECLARATION_START);
        assertThat(section.isEmpty(), is(false));
        assertThat(section.get(202), is("Greece"));
        assertThat(section.get(214), is("Spain"));
        assertThat(section.get(222), is("Italy"));
        assertThat(section.get(310), is("United States"));
        assertThat(section.get(240), is("Sweden"));
    }

    @Test
    public void testFrame2DigitMccMnc() {
        final var section = PacketE212Parser.frameSection(source, PacketE212Parser.MCC_MNC_2DIGITS_CODES_START);
        assertThat(section.isEmpty(), is(false));
        assertThat(section.get(20201), is("Cosmote AE"));
        assertThat(section.get(20202), is("Cosmote AE"));
        assertThat(section.get(20815), is("Free Mobile"));
        assertThat(section.get(20887), is("Régie Autonome des Transports Parisiens"));
        assertThat(section.get(23203), is("T-Mobile Austria GmbH"));
        assertThat(section.get(99999), is("Internal use, example, testing"));
    }

    @Test
    public void testFrame3DigitMccMnc() {
        final var section = PacketE212Parser.frameSection(source, PacketE212Parser.MCC_MNC_3DIGITS_CODES_START);
        assertThat(section.isEmpty(), is(false));
        assertThat(section.get(208500), is("EDF")); // first entry
        assertThat(section.get(1666665), is("Unset")); // last entry (Besides null)

        assertThat(section.get(722070), is("Telefónica Comunicaciones Personales S.A."));
        assertThat(section.get(344930), is("AT&T Wireless (Antigua)"));
        assertThat(section.get(311870), is("Sprint"));
        assertThat(section.get(310410), is("AT&T Mobility"));
    }

    @Test
    public void testSeek() {
        ensureSeek(PacketE212Parser.E212_CODES_DECLARATION_START);
        ensureSeek(PacketE212Parser.MCC_MNC_2DIGITS_CODES_START);
        ensureSeek(PacketE212Parser.MCC_MNC_3DIGITS_CODES_START);
    }

    private int ensureSeek(final String startSection) {
        return PacketE212Parser.seek(0, source, PacketE212Parser.E212_CODES_DECLARATION_START)
                .orElseThrow(() -> new RuntimeException("Unit Test Error: Expected to find the index of the line " + startSection));
    }

    @Test
    public void testLoadResource() throws Exception {
        final var lines = PacketE212Parser.loadResource(getClass().getClassLoader(), PacketE212Parser.DEFAULT_E212_SOURCE_CODE_FILENAME);
        assertThat(lines.isEmpty(), is(false));
    }
}