package io.snice.codecs.codec.diameter;

import io.snice.buffer.Buffer;
import io.snice.buffer.ReadableBuffer;
import io.snice.codecs.codec.diameter.avp.AvpHeader;
import io.snice.codecs.codec.diameter.avp.FramedAvp;
import io.snice.codecs.codec.diameter.avp.impl.ImmutableAvpHeader;
import io.snice.codecs.codec.diameter.avp.impl.ImmutableFramedAvp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author jonas@jonasborjesson.com
 */
public class DiameterFramer {

    /**
     * For stream based protocols, we may not get all the data at the same time and as such, we need
     * to wait for more to arrive. This method simply checks if we have enough data in the buffer to
     * fully frame the diameter message.
     *
     * @param buffer
     * @return
     */
    public static boolean canFrameMessage(final Buffer buffer) {
        final ReadableBuffer readable = buffer.toReadableBuffer();
        // need 20 bytes for the header...
        if (readable.getReadableBytes() < 20) {
            return false;
        }
        final DiameterHeader header = DiameterHeader.frame(readable);

        if (header.getLength() > readable.getReadableBytes() + 20) {
            return false;
        }

        return true;
    }


    /**
     * Convenience method for checking if this could indeed be a diameter message. Use this when
     * you just want to check and not handle the {@link DiameterParseException} that would be thrown as a
     * result of this not being a diameter message.
     * <p>
     * TODO: may actually need a more specific ensure exception because right now, you don't konw if
     * it "blew" up because it is not a diameter message or because there is a "real" ensure exception.
     *
     * @param buffer
     * @return
     * @throws IOException
     */
    public static boolean couldBeDiameterMessage(final ReadableBuffer buffer) throws IOException {
        final int index = buffer.getReaderIndex();
        try {
            final DiameterHeader header = DiameterHeader.frame(buffer);
            return header.validate();
        } catch (final DiameterParseException e) {
            return false;
        } finally {
            buffer.setReaderIndex(index);
        }
    }

    public static FramedAvp frameRawAvp(final ReadableBuffer buffer) throws DiameterParseException {
        final AvpHeader header = frameAvpHeader(buffer);
        // final int avpHeaderLength = header.isVendorSpecific() ? 12 : 8;
        // header.getHeaderLength();
        // final int avpHeaderLength = header.getLength();
        final Buffer data = buffer.readBytes(header.getLength() - header.getHeaderLength());
        final FramedAvp avp = new ImmutableFramedAvp(header, data);
        final int padding = avp.getPadding();
        if (padding != 0) {
            buffer.readBytes(padding);
        }

        return avp;
    }


    public static AvpHeader frameAvpHeader(final ReadableBuffer buffer) throws DiameterParseException {
        if (buffer.getReadableBytes() < 8) {
            throw new DiameterParseException("Unable to read 8 bytes from the buffer, not enough data to ensure AVP.");
        }

        // these are the flags and we need to check if the Vendor-ID bit is set and if so we need
        // another 4 bytes for the AVP Header.
        final byte flags = buffer.getByte(buffer.getReaderIndex() + 4);
        final boolean isVendorIdPresent = (flags & 0b10000000) == 0b10000000;
        final Buffer avpHeader = isVendorIdPresent ? buffer.readBytes(12) : buffer.readBytes(8);
        final Optional<Long> vendorId = isVendorIdPresent ? Optional.of(avpHeader.getUnsignedInt(8)) : Optional.empty();
        return new ImmutableAvpHeader(avpHeader, vendorId);

    }

}
