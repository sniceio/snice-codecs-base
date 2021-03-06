package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.Gtpv2InfoElementMetaData;
import liqp.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoElementTemplate {

    private final Template template;

    public static InfoElementTemplate load() throws Exception {
        return new InfoElementTemplate(CodeGen.loadTemplate("templates/gtpv2_information_elements.liquid"));
    }

    private InfoElementTemplate(final Template template) {
        this.template = template;
    }

    public String render(final List<Gtpv2InfoElementMetaData> ies) {
        final var attributes = new HashMap<String, Object>();
        final var elements = new ArrayList<>();
        ies.forEach(elements::add);
        attributes.put("elements", elements);
        return template.render(attributes);
    }

}
