package io.snice.codecs.codegen.diameter.templates;

import io.snice.codecs.codegen.diameter.primitives.AvpPrimitive;
import liqp.RenderSettings;
import liqp.Template;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for finding and loading the appropriate template for an AVP.
 */
public class AvpTemplate {

    private final Template template;
    private final AvpPrimitive avp;

    public static AvpTemplate load(final AvpPrimitive avp) throws URISyntaxException, IOException {
        final URI uri;
        if (avp.isEnumerated()) {
            uri = AvpTemplate.class.getResource("avp_enumerated_template.liquid").toURI();
        } else {
            uri = AvpTemplate.class.getResource("avp_template.liquid").toURI();
        }

        ensureFileSystem(uri);
        final var p = Paths.get(uri);
        final var liquid = Files.readString(p);
        final RenderSettings settings = new RenderSettings.Builder().withStrictVariables(false).build();
        final Template template = Template.parse(liquid).withRenderSettings(settings);
        return new AvpTemplate(avp, template);
    }

    public static FileSystem ensureFileSystem(final URI uri) {
        try {
            if ("jar".equals(uri.getScheme())) {
                final var env = new HashMap<String, Object>();
                env.put("create", "true");
                return FileSystems.newFileSystem(uri, env);
            }
        } catch(final FileSystemAlreadyExistsException | IOException e) {
            // ignore
        }

        return FileSystems.getDefault();

    }

    private AvpTemplate(final AvpPrimitive avp, final Template template) {
        this.avp = avp;
        this.template = template;
    }

    public String render(final Map<String, Object> attributes) {
        return template.render(attributes);
    }


}
