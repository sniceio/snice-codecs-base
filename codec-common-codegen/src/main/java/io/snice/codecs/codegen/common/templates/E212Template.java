package io.snice.codecs.codegen.common.templates;

import io.snice.codecs.codegen.LiquidUtils;
import liqp.Template;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class E212Template {

    private final Template template;

    public static E212Template load() throws Exception {
        return new E212Template(LiquidUtils.loadTemplate("templates/e212.liquid"));
    }

    private E212Template(final Template template) {
        this.template = template;
    }

    public String render(final Map<Integer, String> mcc,
                         final Map<Integer, String> mccMnc2Digit,
                         final Map<Integer, String> mccMnc3Digit) {

        final var attributes = new HashMap<String, Object>();
        final List<Map<String, Object>> mccs = new ArrayList<>();
        final List<Map<String, Object>> mccMnc = new ArrayList<>();

        attributes.put("mccs", mccs);
        attributes.put("mccMncs", mccMnc);

        mcc.entrySet().forEach(entry -> {
            final var map = new HashMap<String, Object>();
            map.put("mcc", entry.getKey());
            map.put("friendlyName", entry.getValue());
            mccs.add(map);
        });

        final var twoDigits = mccMnc2Digit.entrySet().stream().map(e -> mapEntry(mcc, e)).collect(Collectors.toList());
        final var threeDigits = mccMnc3Digit.entrySet().stream().map(e -> mapEntry(mcc, e)).collect(Collectors.toList());
        mccMnc.addAll(twoDigits);
        mccMnc.addAll(threeDigits);
        mccMnc.sort(Comparator.comparingInt(o -> (int) o.get("mccMnc")));

        return template.render(attributes);
    }

    private static final Map<Integer, List<String>> clashes = new HashMap<>();

    private static Map<String, Object> mapEntry(final Map<Integer, String> mcc, final Map.Entry<Integer, String> entry) {
        final var map = new HashMap<String, Object>();
        final var mccMnc = entry.getKey();
        final var mccMncStr = mccMnc.toString();
        final var mccOnly = Integer.parseInt(mccMncStr.substring(0, 3));
        final var mncOnly = Integer.parseInt(mccMncStr.substring(3));

        map.put("mcc", mccOnly);
        map.put("mccFriendlyName", Optional.ofNullable(mcc.get(mccOnly)).orElse("Unknown"));

        map.put("mnc", mncOnly);

        map.put("mccMnc", mccMnc);
        map.put("friendlyName", entry.getValue());
        map.put("nibbles", mccMncStr.length());
        return map;
    }

}
