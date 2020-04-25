package io.snice.codecs.codegen.diameter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.snice.codecs.codegen.diameter.CodeGenTestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CodeConfigTest {

    @Test
    public void testLoadConfig() throws Exception {
        final var conf = loadConfig("config_test_001.yml");
        assertThat(conf.getSourceRootDir(), is(empty()));
        assertThat(conf.getWiresharkDictionaryRoot(), is(empty()));
        assertThat(conf.getCmdPackageConfig(), is(empty()));
        assertThat(conf.getAppPackageConfig(), is(empty()));

        final var avp =  conf.getAvpPackageConfig().get();
        assertThat(avp.generateAll(), is(false));
        assertIncluded(avp, "User-Name", "Session-Id");
        assertNotIncluded(avp, "Hello-Id", "whatever");
        assertNotExcluded(avp, "Hello");

        final var cmd = loadConfig("config_test_002.yml").getCmdPackageConfig().get();
        assertIncluded(cmd, "Hello", "World", "Nisse");
        assertNotIncluded(cmd, "Foo");
        assertExcluded(cmd, "ULR-Flags", "ULA-Flags", "Visited-PLMN-Id");
    }

    @Test
    public void testCopyConfig() throws Exception{
        final var path = Path.of(".");
        final var wireshark = Path.of(".");
        final var conf = loadConfig("config_test_001.yml").copy()
                .withSourceRoot(path)
                .withWiresharkDictionaryDir(wireshark)
                .build();
        assertThat(conf.getSourceRootDir().get(), is(path));
        assertThat(conf.getWiresharkDictionaryRoot().get(), is(wireshark));
    }

    private void assertIncluded(final CodeConfig2.GenerationConfig conf, final String... included) {
        for (final String s : included) {
            assertThat(conf.isIncluded(s), is(true));
        }
    }

    private void assertNotIncluded(final CodeConfig2.GenerationConfig conf, final String... included) {
        for (final String s : included) {
            assertThat(conf.isIncluded(s), is(false));
        }
    }

    private void assertExcluded(final CodeConfig2.GenerationConfig conf, final String... excluded) {
        for (final String s : excluded) {
            assertThat(conf.isExcluded(s), is(true));
        }
    }

    private void assertNotExcluded(final CodeConfig2.GenerationConfig conf, final String... excluded) {
        for (final String s : excluded) {
            assertThat(conf.isExcluded(s), is(false));
        }
    }

    private CodeConfig2 loadConfig(String resource) throws Exception {
        final InputStream stream = CodeGenTestBase.class.getResourceAsStream(resource);
        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(stream, CodeConfig2.class);
    }
}
