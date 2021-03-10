package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.ClassNameConverter;
import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.Gtpv1InfoElementMetaData;
import liqp.Template;

import java.util.HashMap;
import java.util.Map;

public class TvTemplate {

    private final Template template;

    public static TvTemplate load() throws Exception {
        return new TvTemplate(CodeGen.loadTemplate("templates/gtpv1_tv.liquid"));
    }

    private TvTemplate(final Template template) {
        this.template = template;
    }

    public RenderResult render(final ClassNameConverter converter, final String javaPackageName, final Gtpv1InfoElementMetaData ie) {
        final var attributes = new HashMap<String, Object>();
        final Map<String, Object> javaAttributes = new HashMap<>();
        final Map<String, Object> tlivAttributes = new HashMap<>();
        attributes.put("java", javaAttributes);
        javaAttributes.put("package", javaPackageName);
        javaAttributes.put("tv", tlivAttributes);
        final var names = converter.convertPreserveAbbreviation(ie.getFriendlyName());
        final var className = names.size() == 1 ? names.get(0) : names.get(1);
        tlivAttributes.put("name", className);
        tlivAttributes.put("enum", ie.getEnumName());
        tlivAttributes.put("enum_value", ie.getType());
        tlivAttributes.put("type", ie.getTypeImpl().orElse("RawType"));
        tlivAttributes.put("is_of_int_type", ie.isIntType());

        final String rendered = template.render(attributes);
        return new RenderResult(rendered, className);
    }

    public static class RenderResult {

        private final String result;
        private final String javaClassName;

        private RenderResult(final String result, final String javaClassName) {
            this.result = result;
            this.javaClassName = javaClassName;
        }

        public String getResult() {
            return result;
        }

        public String getJavaClassName() {
            return javaClassName;
        }
    }

}
