package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codec.gtp.v1.common.Format;
import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.Gtpv1InfoElementMetaData;
import liqp.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TlvFramerTemplate {

    private final Template template;

    public static TlvFramerTemplate load() throws Exception {
        return new TlvFramerTemplate(CodeGen.loadTemplate("templates/gtpv1_tlv_framer.liquid"));
    }

    private TlvFramerTemplate(final Template template) {
        this.template = template;
    }

    public String render(final ClassNameConverter converter, final String packageName, final List<Gtpv1InfoElementMetaData> tvs) {
        final var attributes = new HashMap<String, Object>();
        final var elements = new ArrayList<>();
        attributes.put("elements", elements);
        attributes.put("package", packageName);

        tvs.stream().filter(tv -> tv.getFormat() == Format.TLV).forEach(tv -> {
            final Map<String, Object> tvAttributes = new HashMap<>();
            final var names = converter.convertPreserveAbbreviation(tv.getFriendlyName());
            final var className = names.size() == 1 ? names.get(0) : names.get(1);
            tvAttributes.put("name", className);
            elements.add(tvAttributes);
        });

        return template.render(attributes);
    }

}
