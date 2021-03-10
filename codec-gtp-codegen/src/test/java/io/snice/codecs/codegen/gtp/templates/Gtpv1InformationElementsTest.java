package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codec.gtp.v1.common.Format;
import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.Gtpv1InfoElementMetaData;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Gtpv1InformationElementsTest {

    @Test
    public void testLoadMetaData() throws Exception {
        final var ies = CodeGen.loadGtpv1InfoElementMetaData();
        assertIE(ies, 1, Format.TV, 1, "CAUSE");
        assertIE(ies, 128, Format.TLV, -1, "END_USER_ADDRESS");
        assertIE(ies, 134, Format.TLV, -1, "MSISDN");

        final var i = ies.stream().filter(ie -> ie.getFriendlyName().contains("International")).findFirst().get();
        System.err.println(i.getEnumName());
    }

    @Test
    public void testRendering() throws Exception {
        final var template = Gtpv1InformationElementsTemplate.load();
        final var ies = CodeGen.loadGtpv1InfoElementMetaData();
        final var rendered = template.render(ies);
        System.out.println(rendered);
    }

    private void assertIE(final List<Gtpv1InfoElementMetaData> ies, final int type,
                          final Format expectedFormat, final int expectedOctets,
                          final String expectedEnumName) {
        final var ie = ies.stream().filter(i -> i.getType() == type)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unit Test Error. Cant find IE of type " + type));

        assertThat(ie.getFormat(), is(expectedFormat));
        assertThat(ie.getNumberOfFixedOctets(), is(expectedOctets));
        assertThat(ie.getEnumName(), is(expectedEnumName));
    }
}
