package io.snice.codecs.codegen.gtp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.snice.codecs.codegen.ClassNameConverter;

import java.util.Optional;

/**
 * This will read a yml file that has been produced from the Information Elements in TS 29.060. See
 * the scripts folder for more information of how to read those files.
 */
// @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Gtpv1InfoElementMetaData {

    @JsonProperty
    private Format format;

    @JsonProperty
    private String informationElement;

    @JsonProperty
    private String lengthType;

    @JsonProperty
    private int numberOfFixedOctets;

    @JsonProperty
    private String reference;

    /**
     * The type is the given type number in the specification.
     */
    @JsonProperty
    private int type;

    /**
     * If not specified, we will use the "raw type" as the underlying implementation.
     */
    @JsonProperty
    private final Optional<String> typeImpl = Optional.empty();

    /**
     * This is converting the Information Element name, as listed in the specification, to a proper
     * java enum name.
     *
     * @return
     */
    public String getEnumName() {
        return ClassNameConverter.defaultConverter().convert(getInformationElement());
    }

    public String getFriendlyName() {
        return getInformationElement();
    }

    public int getType() {
        return type;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public String getInformationElement() {
        return informationElement;
    }

    public void setInformationElement(String informationElement) {
        this.informationElement = informationElement;
    }

    public String getLengthType() {
        return lengthType;
    }

    public void setLengthType(String lengthType) {
        this.lengthType = lengthType;
    }

    public int getNumberOfFixedOctets() {
        return numberOfFixedOctets;
    }

    public void setNumberOfFixedOctets(int numberOfFixedOctets) {
        this.numberOfFixedOctets = numberOfFixedOctets;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Optional<String> getTypeImpl() {
        return typeImpl;
    }

    /**
     * Only two types/formats, Type-Value and Type-Length-Value
     */
    public enum Format {
        TV, TLV;
    }
}
