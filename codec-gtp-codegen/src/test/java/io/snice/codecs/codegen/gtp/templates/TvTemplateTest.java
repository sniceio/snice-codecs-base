package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.CodeGen;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class TvTemplateTest {

    @Test
    public void testLoadTemplate() throws Exception {
        final var template = TlivTemplate.load();
        final var ies = CodeGen.loadInfoElementMetaData();
        final String rendered = template.render(ClassNameConverter.defaultConverter(), ies.get(1));
        assertThat(rendered.length() > 0, CoreMatchers.is(true));
    }

}