package io.snice.codecs.codegen.diameter.primitives;

import io.snice.codecs.codegen.diameter.CodeGenParseException;
import io.snice.codecs.codegen.diameter.DiameterCollector;
import io.snice.codecs.codegen.diameter.Typedef;
import io.snice.codecs.codegen.diameter.builders.AttributeContext;
import io.snice.codecs.codegen.diameter.builders.DiameterSaxBuilder;

import java.util.Objects;

public interface EnumPrimitive extends DiameterPrimitive {

    /**
     * The name of the XML element.
     */
    String NAME = "enum";

    @Override
    default String getElementName() {
        return NAME;
    }

    long getEnumCode();

    String getEnumName();

    @Override
    default EnumPrimitive toEnumPrimitive() throws ClassCastException {
        return this;
    }

    static Builder of(final AttributeContext ctx) throws CodeGenParseException {
        ctx.ensureElementName(NAME);
        // there are some bad values in the wireshark dictionary file.
        // Some spaces and also ' like in "Don't Care" enum value
        final String name = ctx.getString("name").replace("-", "_").replace("(", "_").replace(")", "_").replace(" ", "_").replace("'", "").trim();
        final long code = ctx.getLong("code");
        return new Builder(ctx, name, code);
    }

    class Builder extends DiameterSaxBuilder.BaseBuilder<EnumPrimitive> {

        private final String name;
        private final long code;

        private Builder(final AttributeContext ctx, final String name, final long code) {
            super(ctx);
            this.name = name;
            this.code = code;
        }

        @Override
        public String getElementName() {
            return NAME;
        }


        /**
         * We do not expect that there is a child attribute to the typedefn element.
         *
         * @param child
         */
        @Override
        public void attachChildBuilder(final DiameterSaxBuilder child) {
            throw createException("Unexpected child element");
        }

        @Override
        public EnumPrimitive build(final DiameterCollector ctx) {
            return new DefaultEnumPrimitive(name, code);
        }
    }

    class DefaultEnumPrimitive implements EnumPrimitive {

        private final String name;
        private final long code;

        private DefaultEnumPrimitive(final String name, final long code) {
            this.name = name;
            this.code = code;
        }

        @Override
        public Typedef getTypedef() {
            return Typedef.ENUMERATED;
        }

        @Override
        public long getEnumCode() {
            return code;
        }

        @Override
        public String getEnumName() {
            return name;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final DefaultEnumPrimitive that = (DefaultEnumPrimitive) o;
            return code == that.code;
        }

        @Override
        public int hashCode() {
            return Objects.hash(code);
        }

        @Override
        public String toString() {
            return name + "(" + code + ")";
        }
    }
}
