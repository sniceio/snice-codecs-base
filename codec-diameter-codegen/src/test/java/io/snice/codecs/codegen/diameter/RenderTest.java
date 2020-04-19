package io.snice.codecs.codegen.diameter;

import io.snice.codecs.codegen.diameter.config.CodeConfig;
import org.junit.Before;
import org.junit.Test;

public class RenderTest extends CodeGenTestBase {

    private CodeConfig config;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        config = CodeConfig.of().build();
    }

    @Test
    public void testConvertElement() throws Exception {
        final DiameterCollector collector = load("avp001.xml");
        final Renderer renderer = new Renderer(config, collector);
        final String rendered = renderer.renderAvp("Origin-Host");
        // final String rendered = renderer.renderAvp("Vendor-Id");
        // final String rendered = renderer.renderAvp("Framed-MTU");
        // final String rendered = renderer.renderAvp("User-Name");
        // final String rendered = renderer.renderAvp("Redirect-Host-Usage");
        // final String rendered = renderer.renderAvp("Result-Code");
        // final String rendered = renderer.renderAvp("Result-Code");
        // final String rendered = renderer.renderAvp("Subscription-Data");
        System.out.println(rendered);
    }
}
