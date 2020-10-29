package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.gtp.CodeGen;
import io.snice.codecs.codegen.gtp.InfoElementMetaData;
import io.snice.codecs.codegen.gtp.MessageTypeMetaData;
import liqp.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageTypeTemplate {

    private final Template template;

    public static MessageTypeTemplate load() throws Exception {
        return new MessageTypeTemplate(CodeGen.loadTemplate("templates/gtpv2_message_types.liquid"));
    }

    private MessageTypeTemplate(final Template template) {
        this.template = template;
    }

    public String render(final List<MessageTypeMetaData> ies) {
        final var attributes = new HashMap<String, Object>();
        final var elements = new ArrayList<>();
        ies.forEach(elements::add);
        attributes.put("elements", elements);
        return template.render(attributes);
    }

}
