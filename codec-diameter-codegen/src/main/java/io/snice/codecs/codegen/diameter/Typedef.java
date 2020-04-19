package io.snice.codecs.codegen.diameter;

import io.snice.codecs.codec.diameter.avp.Avp;
import io.snice.codecs.codec.diameter.avp.impl.DiameterEnumeratedAvp;
import io.snice.codecs.codec.diameter.avp.impl.DiameterGroupedAvp;
import io.snice.codecs.codec.diameter.avp.impl.DiameterIdentityAvp;
import io.snice.codecs.codec.diameter.avp.impl.DiameterInteger32Avp;
import io.snice.codecs.codec.diameter.avp.impl.DiameterIpAddressAvp;
import io.snice.codecs.codec.diameter.avp.impl.DiameterOctetStringAvp;
import io.snice.codecs.codec.diameter.avp.impl.DiameterUnsigned32Avp;
import io.snice.codecs.codec.diameter.avp.impl.DiameterUtf8StringAvp;
import io.snice.codecs.codec.diameter.avp.type.DiameterType;
import io.snice.codecs.codec.diameter.avp.type.Enumerated;
import io.snice.codecs.codec.diameter.avp.type.Grouped;
import io.snice.codecs.codec.diameter.avp.type.Integer32;
import io.snice.codecs.codec.diameter.avp.type.Integer64;
import io.snice.codecs.codec.diameter.avp.type.IpAddress;
import io.snice.codecs.codec.diameter.avp.type.OctetString;
import io.snice.codecs.codec.diameter.avp.type.UTF8String;
import io.snice.codecs.codec.diameter.avp.type.Unsigned32;
import io.snice.codecs.codec.diameter.avp.type.DiameterIdentity;

import java.util.Optional;
import java.util.stream.Stream;

public enum Typedef {
    OCTET_STRING("OctetString", null, OctetString.class, DiameterOctetStringAvp.class),
    INTEGER_32("Integer32", null, Integer32.class, DiameterInteger32Avp.class),
    INTEGER_64("Integer64", null, Integer64.class, null),
    UNSIGNED_32("Unsigned32", null, Unsigned32.class, DiameterUnsigned32Avp.class),
    UNSIGNED_64("Unsigned64", null, null, null),
    FLOAT_32("Float32", null, null, null),
    FLOAT_64("Float64", null, null, null),
    TIME("Time", null, null, null),

    UTF8_STRING("UTF8String", OCTET_STRING, UTF8String.class, DiameterUtf8StringAvp.class),
    ENUMERATED("Enumerated", INTEGER_32, Enumerated.class, DiameterEnumeratedAvp.class),
    GROUPED("Grouped", null, Grouped.class, DiameterGroupedAvp.class),
    GAVP("Gavp", null, Grouped.class, DiameterGroupedAvp.class),
    DIAMETER_URI("DiameterURI", UTF8_STRING, null, null),
    IP_ADDRESS("IPAddress", OCTET_STRING, IpAddress.class, DiameterIpAddressAvp.class),
    DIAMETER_IDENTITY("DiameterIdentity", OCTET_STRING, DiameterIdentity.class, DiameterIdentityAvp.class),
    IP_FILTER_RULE("IPFilterRule", OCTET_STRING, null, null),
    QoS_FILTER_RULE("QoSFilterRule", OCTET_STRING, null, null),
    MIP_REGISTRATION_REQUEST("MIPRegistrationRequest", OCTET_STRING, null, null),
    VENDOR_ID("VendorId", UNSIGNED_32, Unsigned32.class, DiameterUnsigned32Avp.class),
    APP_ID("AppId", UNSIGNED_32, Unsigned32.class, DiameterUnsigned32Avp.class);

    public static Typedef fromName(final String name) {
        return Stream.of(Typedef.values())
                .filter(t -> name.equalsIgnoreCase(t.name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum constant " + Typedef.class.getCanonicalName() + "." + name));
    }

    private final Optional<Class<? extends DiameterType>> implementingInterface;
    private final Optional<Class<? extends Avp>> implementingClass;
    private final Typedef parent;
    private final String name;

    public String getName() {
        return name;
    }

    public Optional<Class<? extends Avp>> getImplementingClass() {
        return implementingClass;
    }

    public Optional<Class<? extends DiameterType>> getImplementingInterface() {
        return implementingInterface;
    }

    public boolean isUnsigned32() {
        return this == UNSIGNED_32;
    }

    public boolean isInteger32() {
        return this == INTEGER_32;
    }

    public boolean isEnumerated() {
        return this == ENUMERATED;
    }

    Typedef(final String name, final Typedef parent,
            final Class<? extends DiameterType> implementingInterface,
            final Class<? extends Avp> implementingClass) {
        this.implementingInterface = Optional.ofNullable(implementingInterface);
        this.implementingClass = Optional.ofNullable(implementingClass);
        this.name = name;
        this.parent = parent;
    }

    public Typedef getBaseType() {
        return parent != null ? parent : this;
    }

}
