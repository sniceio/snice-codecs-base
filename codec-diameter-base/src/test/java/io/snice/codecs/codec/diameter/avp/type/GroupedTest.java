package io.snice.codecs.codec.diameter.avp.type;

public class GroupedTest {

    /*
    @Test
    public void testCreateGrouped() {
        final List<Avp> list = List.of(defaultProductName, defaultHostIpAddress, defaultOriginHost);

        final var grouped = Grouped.of(list);
        final var productName = grouped.getFramedAvp(ProductName.CODE).get().toProductName();
        assertThat(productName.isProductName(), is(true));

        final Avp.Builder<Grouped> builder =
                Avp.ofType(Grouped.class)
                        .withValue(grouped)
                        .withAvpCode(ExperimentalResult.CODE)
                        .isMandatory(AvpMandatory.MUST.isMandatory())
                        .isProtected(AvpProtected.MUST_NOT.isProtected())
                        .withVendor(Vendor.NONE);

        final Avp<Grouped> avp = builder.build();
        assertThat(avp.getCode(), is((long)ExperimentalResult.CODE));

        // write it out
        final WritableBuffer buffer = WritableBuffer.of(avp.getLength());
        avp.writeTo(buffer);

        // parse it back and ensure it is all working fine.
        final ReadableBuffer b = buffer.build().toReadableBuffer();
        final var rawAvp = DiameterParser.frameRawAvp(b);
        final AvpHeader header = rawAvp.getHeader();
        assertThat(header.getCode(), is((long)ExperimentalResult.CODE));
        assertThat(header.isMandatory(), is(true));
        assertThat(header.isProtected(), is(false));

        final ExperimentalResult result = rawAvp.ensure().toExperimentalResult();
        assertThat(result.isExperimentalResult(), is(true));
        final Grouped g = result.getValue();

        final ProductName pn = g.getFramedAvp(ProductName.CODE).get().ensure().toProductName();
        assertThat(pn.getValue().getValue(), is("Snice Unit Test Product"));

        final HostIpAddress ip = g.getFramedAvp(HostIpAddress.CODE).get().ensure().toHostIpAddress();
        assertThat(ip, is(defaultHostIpAddress));

        final OriginHost oh = g.getFramedAvp(OriginHost.CODE).get().ensure().toOriginHost();
        assertThat(oh, is(defaultOriginHost));
    }
    */

}