package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.Gtpv1InfoElementMetaData;
import io.snice.codecs.codegen.gtp.Gtpv1MessageTypeMetaData;
import liqp.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Gtpv1InformationElementsTemplate {

    private final Template template;

    public static Gtpv1InformationElementsTemplate load() throws Exception {
        return new Gtpv1InformationElementsTemplate(CodeGen.loadTemplate("templates/gtpv1_information_elements.liquid"));
    }

    private Gtpv1InformationElementsTemplate(final Template template) {
        this.template = template;
    }

    public String render(final List<Gtpv1InfoElementMetaData> ies) {
        final var attributes = new HashMap<String, Object>();
        final var elements = new ArrayList<>();
        ies.forEach(elements::add);
        attributes.put("elements", elements);
        return template.render(attributes);
    }

}
