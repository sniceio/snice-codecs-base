package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.gtp.CodeGen;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class Gtpv1MessageTypeTemplateTest {

    @Test
    public void testLoadTemplate() throws Exception {
        final var template = Gtpv1MessageTypeTemplate.load();
        final var ies = CodeGen.loadGtpv1MessageTypeMetaData();
        final String rendered = template.render(ies);
        System.out.println(rendered);
        assertThat(rendered.length() > 0, is(true));
    }

}