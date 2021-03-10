package io.snice.codecs.codegen;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Simple interface that takes the name of a diameter element, such as an AVP, a command or application,
 * and converts the name, which usually comes the dictionary xml files, into a Java class name.
 * <p>
 * Note: this class is mainly focusing on the patterns of names that appear in the various 3GPP specs for
 * Diameter and GTP. As such, it is not a perfect, nor meant to me, converter for all possible use cases.
 * It's the bare minimum for it to be useful without having to manually convert the names in those specs.
 */
public interface ClassNameConverter {

    static ClassNameConverter defaultConverter() {
        return new DefaultClassNameConverter();
    }

    String convert(String name);

    /**
     * Oh java, why can't you allow to return more than one value!
     *
     * Anyway, will convert the given class name into a proper java one
     * but if there is anything within parentheses, it will be preserved
     * as an "abbreviation" and returned as the second element in the given list.
     *
     * E.g. "International Mobile Subscriber Identity (IMSI)"  has abbreviation specified (IMSI)
     * which will be returned as the second element.
     *
     * @param name
     * @return a list of either a single value, in which case the given name didn't have an "abbreviation"
     *         and a list of two values, if the given name did have an abbreviation in it.
     */
    List<String> convertPreserveAbbreviation(String name);

    /**
     * Converts the name to a proper enum name (all caps and snake cased) and honors
     * any abbreviations. E.g., in the 3GPP specs, it typically lists IMSI as such:
     * "International Mobile Subscriber Identity (IMSI)" and since the abbreviation IMSI
     * is common, we prefer to use the abbreviation as opposed to the "long" name. I.e., instead of
     * ending up with "INTERNATIONAL_MOBILE_SUBSCRIBER_IDENTITY" it favors the abbreviation IMSI.
     *
     * @param name
     * @return
     */
    String convertToEnum(String name);

    /**
     * Default rule is simply of course camel case, no '-' and also, I don't like the
     * class name that has abbreviations and those are kept in upper case. E.g. the AVP
     * "Outgoing-Trunk-Group-ID" may be named OutgoingTrunkGroupID but this one will convert it
     * to OutgoingTrunkGroupId, note how the last ID is "Id", i.e. capital 'i' but lowercase 'd'.
     * <p>
     * Furthermore, occasionally, the definitions have abbreviated versions of the name within parentheses, e.g.
     * "International Mobile Subscriber Identity (IMSI)" and in those cases, we will remove anything within
     * those parentheses. That word will be saved to the abbreviation
     */
    class DefaultClassNameConverter implements ClassNameConverter {

        private final Map<String, String> exceptions = new HashMap<>();

        private DefaultClassNameConverter() {

            // a bunch of exceptions and some patterns that I felt is just easier to override
            exceptions.put("Recovery (Restart Counter)".toLowerCase(), "RECOVERY");
            exceptions.put("Rat Type".toLowerCase(), "RAT");
            exceptions.put("PDN Type".toLowerCase(), "PDN");

            exceptions.put("MM Context (GSM Key and Triplets)".toLowerCase(), "MM_CONTEXT_GSM_KEY_TRIPLETS");
            exceptions.put("MM Context (GSM Key and Triplets)".toLowerCase(), "MM_CONTEXT_GSM_KEY_TRIPLETS");
            exceptions.put("MM Context (UMTS Key, Used Cipher and Quintuplets)".toLowerCase(), "MM_CONTEXT_UMTS_KEY_USED_CIPHER_QUINTUPLETS");
            exceptions.put("MM Context (GSM Key,Used Cipher and Quintuplets)".toLowerCase(), "MM_CONTEXT_GSM_KEY_USED_CIPHER_QUINTUPLETS");
            exceptions.put("MM Context (UMTS Key and Quintuplets)".toLowerCase(), "MM_CONTEXT_UMTS_KEY_QUINTUPLETS");
            exceptions.put("MM Context (EPS Security Context,Quadruplets and Quintuplets)".toLowerCase(), "MM_CONTEXT_EPS_SECURITY_CONTEXT_QUADRUPLETS_QUINTUPLETS");
            exceptions.put("MM Context (UMTS Key, Quadruplets and Quintuplets)".toLowerCase(), "MM_CONTEXT_UMTS_KEY_QUADRUPLETS_QUINTUPLETS");
            exceptions.put("Special IE type for IE Type Extension".toLowerCase(), "EXTENSION");
            exceptions.put("Extended Protocol Configuration Options (ePCO)".toLowerCase(), "EPCO");
            exceptions.put("F-Container".toLowerCase(), "F_CONTAINER");
            exceptions.put("F-Cause".toLowerCase(), "F_CAUSE");
            exceptions.put("H(e)NB Information Reporting".toLowerCase(), "HENB_INFORMATION_REPORTING");
        }

        private Optional<String> hasManualOverride(final String name) {
            return Optional.ofNullable(exceptions.get(name.toLowerCase()));
        }

        @Override
        public List<String> convertPreserveAbbreviation(final String name) {

            final var exception = hasManualOverride(name);
            if (exception.isPresent()) {
                return List.of(exception.get());
            }

            // personal style. I prefer to type out 2G3G instead of 2G_3G
            // and the QoS stuff was an easy ugly fix to just have it stay "together"
            final String origName = name.replaceAll("2G/3G", "2G3G")
                    .replaceAll("QoS", "Qos");
            final char[] className = new char[origName.length()];
            int index = 0;

            // first character is always upper case.
            boolean toUpperCase = true;

            boolean inAbbreviation = false;
            String abbreviation = "";

            for (int i = 0; i < origName.length(); ++i) {
                final char ch = origName.charAt(i);
                // ensure that the first character isn't an illegal java classname one.
                // if it is, translate if if we have a good translation and if not, complain.
                // If you feel that you have a good translation, just add code here.
                if (i == 0 && Character.isDigit(ch)) {
                    if (ch == '3') {
                        className[index++] = 'T';
                    } else {
                        throw new IllegalArgumentException("A Java class name cannot start " +
                                "with a digit and I currently do not have a good translation for the digit " + ch +
                                ". If you have one, please update the code in "
                                + ClassNameConverter.class.getCanonicalName());
                    }
                    toUpperCase = false;
                    continue;
                }


                // next should be upper case and the current character will be skipped.
                if (ch == '-' || ch == '_' || ch == '/' || Character.isWhitespace(ch)) {
                    toUpperCase = true;
                    if (inAbbreviation) {
                        abbreviation += "_";
                    }
                    continue;
                } else if (ch == '(') {
                    if (!abbreviation.isEmpty()) {
                        throw new IllegalArgumentException("Detected two so-called abbreviations within " +
                                "the given name. I can only handle a single one");
                    }

                    inAbbreviation = true;
                    toUpperCase = false;
                    continue;
                } else if (ch == ')') {
                    inAbbreviation = false;
                    continue;
                }

                if (inAbbreviation) {
                    abbreviation += ch;
                } else {
                    className[index++] = toUpperCase ? Character.toUpperCase(ch) : Character.toLowerCase(ch);
                }
                toUpperCase = false;
            }

            final char[] result = new char[index];
            System.arraycopy(className, 0, result, 0, index);
            final var javaClassName = new String(result);
            if (abbreviation.isEmpty()) {
                return List.of(patch(javaClassName));
            }

            final var finalClassName = patch(javaClassName);

            // this is a bit dumb but it works. Perhaps I'll fix it at some point
            final var finalAbbreviation = enumToClassName(convertToEnum(patch(abbreviation)));
            return List.of(finalClassName, finalAbbreviation);
        }

        /**
         * Convert a proper enum name to a class name.
         *
         * @param enumName
         * @return
         */
        private String enumToClassName(final String enumName) {
            return Arrays.stream(enumName.toLowerCase().split("_"))
                    .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                    .collect(Collectors.joining());
        }

        /**
         * Some abbreviations we just want a certain way and since it is not like the
         * names in the various 3GPP specs follow a well-defined ABNF and therefore, hard to
         * write generic rules for everything. So, just patch it...
         *
         * @param name
         * @return
         */
        private String patch(final String name) {
            return name.replaceAll("FTEID", "F_TEID")
                    .replaceAll("PTMSI", "P_TMSI");
        }


        @Override
        public String convertToEnum(final String name) {
            final var names = convertPreserveAbbreviation(name);
            if (names.size() == 1) {
                return patch(toEnumName(names.get(0)));
            }
            return patch(toEnumName(names.get(1)));
        }

        private String toEnumName(final String name) {
            final var sb = new StringBuilder();
            for (int i = 0; i < name.length() - 1; ++i) {
                final var ch = name.charAt(i);
                final var peek = name.charAt(i + 1);
                sb.append(Character.toUpperCase(ch));
                if (Character.isLowerCase(ch) && Character.isUpperCase(peek)) {
                    sb.append("_");
                }
            }
            sb.append(Character.toUpperCase(name.charAt(name.length() - 1)));
            return sb.toString();
        }

        @Override
        public String convert(final String name) {
            return convertPreserveAbbreviation(name).get(0);
        }
    }

}
