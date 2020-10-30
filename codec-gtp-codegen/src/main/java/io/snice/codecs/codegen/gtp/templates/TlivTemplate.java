package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.InfoElementMetaData;
import liqp.Template;

import java.util.HashMap;
import java.util.Map;

public class TlivTemplate {

    private final Template template;

    public static TlivTemplate load() throws Exception {
        return new TlivTemplate(CodeGen.loadTemplate("templates/gtpv2_tliv.liquid"));
    }

    private TlivTemplate(final Template template) {
        this.template = template;
    }

    public String render(final ClassNameConverter converter, final InfoElementMetaData ie) {
        final var attributes = new HashMap<String, Object>();
        final Map<String, Object> javaAttributes = new HashMap<>();
        final Map<String, Object> tlivAttributes = new HashMap<>();
        attributes.put("java", javaAttributes);
        javaAttributes.put("tliv", tlivAttributes);
        tlivAttributes.put("name", converter.convert(ie.getEnumValue()));
        tlivAttributes.put("enum", ie.getEnumValue());
        tlivAttributes.put("type", ie.getTypeImpl().orElse("RawType"));

        return template.render(attributes);
    }

}