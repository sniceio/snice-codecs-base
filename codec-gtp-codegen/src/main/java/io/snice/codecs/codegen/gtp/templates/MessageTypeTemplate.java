package io.snice.codecs.codegen.gtp.templates;

import io.snice.codecs.codegen.gtp.CodeGen;
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
        ies.forEach(ie -> {

            // if the 'request' value isn't explicitly set, then let's try and figure
            // out if this is considered a request or a response.
            if (ie.isRequest() == null) {
                if (ie.getMessage().contains("REQUEST")) {
                    ie.isRequest(true);
                } else if (ie.getMessage().contains("RESPONSE")) {
                    ie.isRequest(false);
                } else if (ie.getMessage().contains("ACKNOWLEDGE")) {
                    ie.isRequest(false);
                } else if (ie.getMessage().contains("NOTIFICATION")) {
                    ie.isRequest(true);
                }
            }
            elements.add(ie);
        });
        attributes.put("elements", elements);
        return template.render(attributes);
    }

}
