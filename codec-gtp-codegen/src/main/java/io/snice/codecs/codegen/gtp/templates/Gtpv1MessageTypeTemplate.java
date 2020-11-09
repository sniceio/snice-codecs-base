package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.Gtpv1MessageTypeMetaData;
import liqp.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Gtpv1MessageTypeTemplate {

    private final Template template;

    public static Gtpv1MessageTypeTemplate load() throws Exception {
        return new Gtpv1MessageTypeTemplate(CodeGen.loadTemplate("templates/gtpv1_message_types.liquid"));
    }

    private Gtpv1MessageTypeTemplate(final Template template) {
        this.template = template;
    }

    public String render(final List<Gtpv1MessageTypeMetaData> ies) {
        final var attributes = new HashMap<String, Object>();
        final var elements = new ArrayList<>();
        ies.forEach(elements::add);
        attributes.put("elements", elements);
        return template.render(attributes);
    }

}
