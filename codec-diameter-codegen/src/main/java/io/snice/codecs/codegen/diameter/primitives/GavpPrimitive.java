package io.snice.codecs.codegen.diameter.primitives;

import io.snice.codecs.codegen.diameter.CodeGenParseException;
import io.snice.codecs.codegen.diameter.DiameterCollector;
import io.snice.codecs.codegen.diameter.Typedef;
import io.snice.codecs.codegen.diameter.builders.AttributeContext;
import io.snice.codecs.codegen.diameter.builders.DiameterSaxBuilder;

public interface GavpPrimitive extends DiameterPrimitive {

    /**
     * The name of the XML element.
     */
    String NAME = "gavp";

    @Override
    default Typedef getTypedef() {
        return Typedef.GAVP;
    }

    @Override
    default String getElementName() {
        return NAME;
    }

    @Override
    default GavpPrimitive toGavpPrimitive() throws ClassCastException {
        return this;
    }

    /**
     * The name of the AVP that is part of a {@link GroupedPrimitive}
     *
     * @return
     */
    String getName();

    static Builder of(final AttributeContext ctx) throws CodeGenParseException {
        ctx.ensureElementName(NAME);
        final String name = ctx.getString("name");
        return new Builder(ctx, name);
    }

    class Builder extends DiameterSaxBuilder.BaseBuilder<GavpPrimitive> {

        private final String name;

        private Builder(final AttributeContext ctx, final String name) {
            super(ctx);
            this.name = name;
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
        public GavpPrimitive build(final DiameterCollector ctx) {
            return () -> name;
        }
    }

}
