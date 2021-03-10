package io.snice.codecs.codegen.gtp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.snice.codecs.codec.gtp.v1.common.Format;
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
     * Whether or not this type can be represented, or parsed from, as an integer. Mainly used
     * for code generation.
     */
    @JsonProperty
    private boolean isIntType = false;

    public boolean isIntType() {
        return isIntType;
    }

    public void setIntType(final boolean intType) {
        isIntType = intType;
    }

    /**
     * This is converting the Information Element name, as listed in the specification, to a proper
     * java enum name.
     *
     * @return
     */
    public String getEnumName() {
        return ClassNameConverter.defaultConverter().convertToEnum(getInformationElement());
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

    public void setFormat(final Format format) {
        this.format = format;
    }

    public String getInformationElement() {
        return informationElement;
    }

    public void setInformationElement(final String informationElement) {
        this.informationElement = informationElement;
    }

    public String getLengthType() {
        return lengthType;
    }

    public void setLengthType(final String lengthType) {
        this.lengthType = lengthType;
    }

    public int getNumberOfFixedOctets() {
        return numberOfFixedOctets;
    }

    public void setNumberOfFixedOctets(final int numberOfFixedOctets) {
        this.numberOfFixedOctets = numberOfFixedOctets;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public void setType(final int type) {
        this.type = type;
    }

    public Optional<String> getTypeImpl() {
        return typeImpl;
    }

}
