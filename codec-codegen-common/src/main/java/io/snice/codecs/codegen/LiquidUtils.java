package io.snice.codecs.codegen;

import liqp.RenderSettings;
import liqp.Template;

import java.nio.file.Files;
import java.nio.file.Paths;

import static io.snice.codecs.codegen.FileSystemUtils.getURL;

public class LiquidUtils {

    public static Template loadTemplate(final String file) throws Exception {
        final var url = getURL(file);
        final var p = Paths.get(url.toURI());
        final var liquid = Files.readString(p);
        final RenderSettings settings = new RenderSettings.Builder().withStrictVariables(false).build();
        return Template.parse(liquid).withRenderSettings(settings);
    }

}
