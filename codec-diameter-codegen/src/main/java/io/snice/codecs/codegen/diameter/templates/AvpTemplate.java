package io.snice.codecs.codegen.diameter.templates;

import io.snice.codecs.codegen.diameter.config.CodeConfig;
import io.snice.codecs.codegen.diameter.primitives.AvpPrimitive;
import liqp.RenderSettings;
import liqp.Template;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Helper class for finding and loading the appropriate template for an AVP.
 */
public class AvpTemplate {

    private final Template template;
    private final AvpPrimitive avp;

    public static AvpTemplate load(final AvpPrimitive avp) throws URISyntaxException, IOException {
        final Path p;
        if (avp.isEnumerated()) {
            p = Paths.get(AvpTemplate.class.getResource("avp_enumerated_template.liquid").toURI());
        } else {
            p = Paths.get(AvpTemplate.class.getResource("avp_template.liquid").toURI());
        }

        final RenderSettings settings = new RenderSettings.Builder().withStrictVariables(false).build();
        final Template template = Template.parse(p.toFile()).withRenderSettings(settings);

        return new AvpTemplate(avp, template);
    }

    private AvpTemplate(final AvpPrimitive avp, final Template template) {
        this.avp = avp;
        this.template = template;
    }

    public String render(final CodeConfig baseConfig) {
        return render(baseConfig.createAvpConfig(avp).getAttributes());
    }

    public String render(final Map<String, Object> attributes) {
        return template.render(attributes);
    }


}
