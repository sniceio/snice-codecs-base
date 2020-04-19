package io.snice.codecs.codegen.diameter.primitives;

import io.snice.codecs.codegen.diameter.CodeGenParseException;
import io.snice.codecs.codegen.diameter.DiameterCollector;
import io.snice.codecs.codegen.diameter.builders.AttributeContext;
import io.snice.codecs.codegen.diameter.builders.DiameterSaxBuilder;

import java.util.Optional;

public interface ApplicationPrimitive extends DiameterPrimitive {

    /**
     * The name of the XML element.
     */
    String NAME = "application";

    @Override
    default String getElementName() {
        return NAME;
    }

    static Builder of(final AttributeContext ctx) throws CodeGenParseException {
        ctx.ensureElementName(NAME);
        final long appId = ctx.getLong("id");
        final String name = ctx.getString("name");
        final Optional<String> uri = ctx.getOptionalString("uri");
        return new Builder(ctx, appId, name, uri);
    }

    class Builder extends DiameterSaxBuilder.BaseBuilder<ApplicationPrimitive> {

        private final long appId;
        private final String name;
        private final Optional<String> uri;

        private Builder(final AttributeContext ctx, final long appId, final String name, final Optional<String> uri) {
            super(ctx);
            this.appId = appId;
            this.name = name;
            this.uri = uri;
        }

        @Override
        public String getElementName() {
            return NAME;
        }

        @Override
        public ApplicationPrimitive build(final DiameterCollector ctx) {
            return null;
        }

    }
}
