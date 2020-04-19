package io.snice.codecs.codegen.diameter.primitives;

import io.snice.codecs.codegen.diameter.CodeGenParseException;
import io.snice.codecs.codegen.diameter.DiameterCollector;
import io.snice.codecs.codegen.diameter.Typedef;
import io.snice.codecs.codegen.diameter.builders.AttributeContext;
import io.snice.codecs.codegen.diameter.builders.DiameterSaxBuilder;

public interface TypePrimitive extends DiameterPrimitive {

    /**
     * The name of the XML element.
     */
    String NAME = "type";

    @Override
    default String getElementName() {
        return NAME;
    }

    @Override
    default TypePrimitive toTypePrimitive() throws ClassCastException {
        return this;
    }

    static Builder of(final AttributeContext ctx) throws CodeGenParseException {
        ctx.ensureElementName(NAME);
        final Typedef typedef = Typedef.fromName(ctx.getString("type-name"));
        return new Builder(ctx, typedef);
    }

    class Builder extends DiameterSaxBuilder.BaseBuilder<TypePrimitive> {

        private final Typedef typedef;

        private Builder(final AttributeContext ctx, final Typedef typedef) {
            super(ctx);
            this.typedef = typedef;
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
        public TypePrimitive build(final DiameterCollector ctx) {
            return () -> typedef;
        }
    }
}
