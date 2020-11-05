package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.gtp.CodeGen;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class InfoElementTemplateTest {

    @Test
    public void testLoadTemplate() throws Exception {
        final var template = InfoElementTemplate.load();
        final var ies = CodeGen.loadInfoElementMetaData();
        final String rendered = template.render(ies);
        System.out.println(rendered);
        assertThat(rendered.length() > 0, CoreMatchers.is(true));
    }

}