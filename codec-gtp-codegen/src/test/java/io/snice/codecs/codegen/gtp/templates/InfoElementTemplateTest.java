package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.Gtpv1InfoElementMetaData;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class InfoElementTemplateTest {

    private ClassNameConverter classNameConverter;
    private TvTemplate tvTemplate;
    private List<Gtpv1InfoElementMetaData> tvs;

    @Before
    public void setUp() throws Exception {
        classNameConverter = ClassNameConverter.defaultConverter();
        tvTemplate = TvTemplate.load();
        tvs = CodeGen.loadGtpv1InfoElementMetaData();
    }

    private void testPackageName() {
        tvs.forEach(tv -> {
            final var result = tvTemplate.render(classNameConverter, "unit.test.fake.package.name", tv);
            assertThat(result.getResult().split("\n")[0].contains("package unit.test.fake.package.name;"), is(true));
        });
    }

    @Test
    public void testLoadTemplate() throws Exception {
        ensureInterfaceName(1, "Cause");
        final var recovery = tvTemplate.render(classNameConverter, "unit.test.fake.package.name", get(tvs, 14));
        assertThat(recovery.getResult().contains("public interface Recovery"), is(true));

        // recovery is really just an int (or actually byte) and as such, it should have a static ofValue method to create
        // it from.
        assertThat(recovery.getResult().contains("static Recovery ofValue(final int value)"), is(true));

        final var tlli = tvTemplate.render(classNameConverter, "unit.test.fake.package.name", get(tvs, 4));
        System.out.println(tlli.getResult());
        assertThat(tlli.getJavaClassName(), is("Tlli"));

        final var imsi = tvTemplate.render(classNameConverter, "unit.test.fake.package.name", get(tvs, 2));
        System.out.println(imsi.getResult());
        assertThat(tlli.getJavaClassName(), is("Tlli"));
    }

    private void ensureInterfaceName(final int tvType, final String expectedName) {
        final var tv = tvTemplate.render(classNameConverter, "unit.test.fake.package.name", get(tvs, tvType));
        assertThat(tv.getJavaClassName(), is(expectedName));
    }

    private Gtpv1InfoElementMetaData get(final List<Gtpv1InfoElementMetaData> list, final int type) {
        return list.stream()
                .filter(tv -> tv.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unit test error. Unable to find the TV of type " + type));
    }

}