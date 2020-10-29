package io.snice.codecs.codegen;

import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.InfoElementMetaData;
import io.snice.codecs.codegen.gtp.MessageTypeMetaData;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CodeGenTest {

    @Test
    public void loadMessageTypeSpecifications() throws Exception {
        final var metaData = CodeGen.loadMessageTypeMetaData();
        assertMessageTypeMetaData(metaData.get(0), false, "RESERVED", "29.274", false, 0);
    }

    @Test
    public void loadSpecifications() throws Exception {
        final var metaData = CodeGen.loadInfoElementMetaData();
        assertMetaData(metaData.get(0), "RESERVED", false, -1, 0);
        assertMetaData(metaData.get(1), "IMSI", false, -1, 1);
        assertThat(metaData.get(1).getComment(), is("Variable Length / 8.3"));

        assertMetaData(metaData.get(7), "EBI", true, 1, 73);
    }

    private void assertMessageTypeMetaData(MessageTypeMetaData m, boolean isInitial, String message,
                                           String specification, boolean isTriggered, int type) {
        assertThat(m.isInitial(), is(isInitial));
        assertThat(m.getMessage(), is(message));
        assertThat(m.getSpecification(), is(specification));
        assertThat(m.isTriggered(), is(isTriggered));
        assertThat(m.getType(), is(type));
    }

    private void assertMetaData(InfoElementMetaData ie, String expectedEnum, boolean extendable, int octets, int type) {
        assertThat(ie.getEnumValue(), is(expectedEnum));
        assertThat(ie.isExtendable(), is(extendable));
        assertThat(ie.getOctets(), is(octets));
        assertThat(ie.getType(), is(type));
    }

}