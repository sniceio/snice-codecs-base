package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.CodeGen;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class TlivTemplateTest {

    @Test
    public void testLoadTemplate() throws Exception {
        final var template = TlivTemplate.load();
        final var ies = CodeGen.loadInfoElementMetaData();
        final String rendered = template.render(ClassNameConverter.defaultConverter(), ies.get(1));
        System.out.println(rendered);
        assertThat(rendered.length() > 0, CoreMatchers.is(true));
    }

}