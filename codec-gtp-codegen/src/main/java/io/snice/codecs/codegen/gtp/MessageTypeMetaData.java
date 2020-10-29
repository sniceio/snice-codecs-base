package io.snice.codecs.codegen.gtp;

import java.util.HashMap;
import java.util.Map;

public class MessageTypeMetaData {

    private boolean initial;
    private String message;
    private String specification;
    private boolean triggered;
    private int type;

    public boolean isInitial() {
        return initial;
    }

    public String getMessage() {
        return message;
    }

    public String getSpecification() {
        return specification;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public int getType() {
        return type;
    }

    public Map<String, Object> toAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("initial", initial);
        attributes.put("message", message);
        attributes.put("specification", specification);
        attributes.put("triggered", triggered);
        attributes.put("type", type);
        return attributes;
    }
}
