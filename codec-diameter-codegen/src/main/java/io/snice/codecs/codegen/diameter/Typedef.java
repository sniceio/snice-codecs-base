package io.snice.codecs.codegen.diameter;

import java.util.Optional;
import java.util.stream.Stream;

public enum Typedef {
    OCTET_STRING("OctetString", null, "OctetString", "DiameterOctetStringAvp"),
    INTEGER_32("Integer32", null, "Integer32", "DiameterInteger32Avp"),
    INTEGER_64("Integer64", null, "Integer64", null),
    UNSIGNED_32("Unsigned32", null, "Unsigned32", "DiameterUnsigned32Avp"),
    UNSIGNED_64("Unsigned64", null, null, null),
    FLOAT_32("Float32", null, null, null),
    FLOAT_64("Float64", null, null, null),
    TIME("Time", null, "Time", "TimeAvp"),

    UTF8_STRING("UTF8String", OCTET_STRING, "UTF8String", "DiameterUtf8StringAvp"),
    ENUMERATED("Enumerated", INTEGER_32, "Enumerated", "DiameterEnumeratedAvp"),
    GROUPED("Grouped", null, "Grouped", "DiameterGroupedAvp"),
    GAVP("Gavp", null, "Grouped", "DiameterGroupedAvp"),
    DIAMETER_URI("DiameterURI", UTF8_STRING, null, null),
    IP_ADDRESS("IPAddress", OCTET_STRING, "IpAddress", "DiameterIpAddressAvp"),
    DIAMETER_IDENTITY("DiameterIdentity", OCTET_STRING, "DiameterIdentity", "DiameterIdentityAvp"),
    IP_FILTER_RULE("IPFilterRule", OCTET_STRING, null, null),
    QoS_FILTER_RULE("QoSFilterRule", OCTET_STRING, null, null),
    MIP_REGISTRATION_REQUEST("MIPRegistrationRequest", OCTET_STRING, null, null),
    VENDOR_ID("VendorId", UNSIGNED_32, "Unsigned32", "DiameterUnsigned32Avp"),
    APP_ID("AppId", UNSIGNED_32, "Unsigned32", "DiameterUnsigned32Avp");

    public static Typedef fromName(final String name) {
        return Stream.of(Typedef.values())
                .filter(t -> name.equalsIgnoreCase(t.name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum constant " + Typedef.class.getCanonicalName() + "." + name));
    }

    private final Optional<String> implementingInterface;
    private final Optional<String> implementingInterfaceFqdn;

    private final Optional<String> implementingClass;
    private final Optional<String> implementingClassFqdn;
    private final Typedef parent;
    private final String name;

    public String getName() {
        return name;
    }

    public Optional<String> getImplementingClass() {
        return implementingClass;
    }

    public Optional<String> getImplementingClassFqdn() {
        return implementingClassFqdn;
    }

    public Optional<String> getImplementingInterface() {
        return implementingInterface;
    }

    public Optional<String> getImplementingInterfaceFqdn() {
        return implementingInterfaceFqdn;
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
            final String implementingInterface,
            final String implementingClass) {


        // Generating code can be a bit of a catch-22 where we want to be dependent
        // on the actual implementing classes but if we are we may create a circular
        // dependency instead. Very much damn if you do, damn if you don't situation.
        // If the implementing package changes, it'll have to be updated here too
        final String implementingPackage = "io.snice.codecs.codec.diameter.avp.type";
        final String implementingPackageImpl = "io.snice.codecs.codec.diameter.avp.impl";

        this.implementingInterface = Optional.ofNullable(implementingInterface);
        this.implementingInterfaceFqdn = this.implementingInterface.map(clazz -> implementingPackage + "." + clazz);

        this.implementingClass = Optional.ofNullable(implementingClass);
        this.implementingClassFqdn = this.implementingClass.map(clazz -> implementingPackageImpl + "." + clazz);

        this.name = name;
        this.parent = parent;
    }

    public Typedef getBaseType() {
        return parent != null ? parent : this;
    }

}
