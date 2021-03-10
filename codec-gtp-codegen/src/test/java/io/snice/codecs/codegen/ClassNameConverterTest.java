package io.snice.codecs.codegen;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClassNameConverterTest {

    private ClassNameConverter converter;

    @Before
    public void setUp() {
        converter = ClassNameConverter.defaultConverter();
    }

    @Test
    public void testRegularClassName() {
        ensureConversion("InternationalMobileSubscriberIdentity", "International Mobile Subscriber Identity (IMSI)", "IMSI");
        ensureConversion("ChargingGatewayAddress", "Charging Gateway Address", null);

        ensureConversion("OutgoingTrunkGroupId", "Outgoing-Trunk-Group-ID", null);

        // many spaces...
        ensureConversion("ChargingGatewayAddress", "    Charging     Gateway Address     ", null);
        // tabs...
        ensureConversion("ChargingGatewayAddress", "Charging    Gateway Address             ", null);

        // don't know why this would ever be specified this way but if it does, the current strategy is still
        // to detect and remove it from the "main" name and return it as an abbreviation.
        ensureConversion("AbbreviationInTheMiddle", "Abbreviation (MIDDLE) In The Middle", "MIDDLE");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTwoAbbreviations() {
        converter.convert("Two Abbreviations (ONE) (TWO)");
    }

    private void ensureConversion(final String expected, final String name, final String abbreviation) {
        final var result = converter.convertPreserveAbbreviation(name);
        assertThat(result.get(0), is(expected));

        // also verify that the method that doesn't preserve the abbreviation does the right thing
        assertThat(converter.convert(name), is(expected));


        if (abbreviation == null) {
            assertThat(result.size(), is(1));
        } else {
            assertThat(result.get(1), is(abbreviation));
        }
    }

}