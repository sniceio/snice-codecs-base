package io.snice.codecs.codegen.diameter.templates;

import io.snice.codecs.codegen.diameter.config.Attributes;
import io.snice.codecs.codegen.diameter.primitives.AvpPrimitive;
import io.snice.preconditions.PreConditions;
import liqp.RenderSettings;
import liqp.Template;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.snice.preconditions.PreConditions.assertArgument;
import static io.snice.preconditions.PreConditions.assertCollectionNotEmpty;

/**
 * Helper class for finding and loading the appropriate template for an AVP.
 */
public class AvpFramerTemplate {

    private final Template template;

    public static AvpFramerTemplate load() throws URISyntaxException, IOException {
        final URI uri = AvpFramerTemplate.class.getResource("avp_framer_template.liquid").toURI();

        AvpTemplate.ensureFileSystem(uri);
        final var p = Paths.get(uri);
        final var liquid = Files.readString(p);
        final RenderSettings settings = new RenderSettings.Builder().withStrictVariables(false).build();
        final Template template = Template.parse(liquid).withRenderSettings(settings);
        return new AvpFramerTemplate(template);
    }

    private AvpFramerTemplate(final Template template) {
        this.template = template;
    }

    public String render(final Attributes framerAttributes, final List<Attributes> avps) {
        assertCollectionNotEmpty(avps);
        final Map<String, Object> map = new HashMap();
        map.put("framer", framerAttributes);
        map.put("avps", avps);
        return template.render(map);
    }


}
