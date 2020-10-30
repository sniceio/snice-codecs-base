package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.InfoElementMetaData;
import liqp.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TlivFramerTemplate {

    private final Template template;

    public static TlivFramerTemplate load() throws Exception {
        return new TlivFramerTemplate(CodeGen.loadTemplate("templates/gtpv2_tliv_framer.liquid"));
    }

    private TlivFramerTemplate(final Template template) {
        this.template = template;
    }

    public String render(final ClassNameConverter converter, final List<InfoElementMetaData> ies) {
        /*
        final var attributes = new HashMap<String, Object>();
        final var elements = new ArrayList<>();
        ies.forEach(elements::add);
        attributes.put("elements", elements);
        return template.render(attributes);
         */

        final var attributes = new HashMap<String, Object>();
        final var elements = new ArrayList<>();
        attributes.put("elements", elements);

        ies.forEach(ie -> {
            final Map<String, Object> tlivAttributes = new HashMap<>();
            tlivAttributes.put("name", converter.convert(ie.getEnumValue()));
            tlivAttributes.put("type", ie.getType());
            elements.add(tlivAttributes);
        });

        return template.render(attributes);
    }

}
