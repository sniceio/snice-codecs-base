package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codec.gtp.v1.common.Format;
import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.Gtpv1InfoElementMetaData;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TvFramerTemplateTest {

    private ClassNameConverter classNameConverter;
    private TvFramerTemplate tvTemplate;
    private List<Gtpv1InfoElementMetaData> tvs;

    @Before
    public void setUp() throws Exception {
        classNameConverter = ClassNameConverter.defaultConverter();
        tvTemplate = TvFramerTemplate.load();
        tvs = CodeGen.loadGtpv1InfoElementMetaData();
    }

    @Test
    public void testGenerateFramer() {
        final var result = tvTemplate.render(classNameConverter, "hello.world", tvs);
        assertThat(result.contains("package hello.world;"), is(true));
        final var countTypeValueElements = tvs.stream().filter(tv -> tv.getFormat() == Format.TV).count();
        final var countCaseStatements = Arrays.stream(result.split("\n")).filter(line -> line.contains("case (")).count();

        // thrice as many because we generate three different switch statements
        assertThat(countCaseStatements, is(countTypeValueElements * 3L));
    }

}