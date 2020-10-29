package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.InfoElementMetaData;
import liqp.RenderSettings;
import liqp.Template;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoElementTemplate {

    private final Template template;

    public static InfoElementTemplate load() throws Exception {
        return new InfoElementTemplate(CodeGen.loadTemplate("templates/gtpv2_information_elements.liquid"));
    }

    private InfoElementTemplate(final Template template) {
        this.template = template;
    }

    public String render(final List<InfoElementMetaData> ies) {
        final var attributes = new HashMap<String, Object>();
        final var elements = new ArrayList<>();
        ies.forEach(elements::add);
        attributes.put("elements", elements);
        return template.render(attributes);
    }

}
