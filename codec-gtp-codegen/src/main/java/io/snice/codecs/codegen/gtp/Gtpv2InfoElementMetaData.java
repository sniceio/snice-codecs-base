package io.snice.codecs.codegen.gtp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.snice.codecs.codegen.ClassNameConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Gtpv2InfoElementMetaData {

    @JsonProperty
    private String comment;

    /**
     * Note that this enum value as found in the yml file is no longer really used.
     * The actual name of the resulting Enum is derived from the friendly name.
     *
     * TODO: remove this one and remove it from the yaml file.
     */
    @JsonProperty("enum")
    private String enumValue;

    @JsonProperty
    private boolean extendable;

    @JsonProperty
    private String friendlyName;

    @JsonProperty
    private int octets;

    @JsonProperty
    private int type;

    @JsonProperty
    private final Optional<String> typeImpl = Optional.empty();

    public String getComment() {
        return comment;
    }

    public String getEnumValue() {
        return getName();
    }

    public boolean isExtendable() {
        return extendable;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public int getOctets() {
        return octets;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        // return ClassNameConverter.defaultConverter().convert(enumValue);
        return ClassNameConverter.defaultConverter().convertToEnum(friendlyName);
    }

    public Optional<String> getTypeImpl() {
        return typeImpl;
    }

    public Map<String, Object> toAttributes() {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("comment", comment);
        // attributes.put("enum", enumValue);
        attributes.put("enum", getName());
        attributes.put("enum_byte", getType());
        attributes.put("extendable", extendable);
        attributes.put("friendly_name", friendlyName);
        attributes.put("octets", octets);
        attributes.put("type", type);
        return attributes;
    }
}
