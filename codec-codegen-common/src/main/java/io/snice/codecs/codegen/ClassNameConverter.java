package io.snice.codecs.codegen;

import java.util.List;

/**
 * Simple interface that takes the name of a diameter element, such as an AVP, a command or application,
 * and converts the name, which usually comes the dictionary xml files, into a Java class name.
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
     * Default rule is simply of course camel case, no '-' and also, I don't like the
     * class name that has abbreviations and those are kept in upper case. E.g. the AVP
     * "Outgoing-Trunk-Group-ID" may be named OutgoingTrunkGroupID but this one will convert it
     * to OutgoingTrunkGroupId, note how the last ID is "Id", i.e. capital 'i' but lowercase 'd'.
     *
     * Furthermore, occasionally, the definitions have abbreviated versions of the name within parentheses, e.g.
     * "International Mobile Subscriber Identity (IMSI)" and in those cases, we will remove anything within
     * those parentheses. That word will be saved to the abbreviation
     *
     */
    class DefaultClassNameConverter implements ClassNameConverter {

        @Override
        public List<String> convertPreserveAbbreviation(String name) {
            // final String origName = name.toLowerCase();
            final String origName = name; // .toLowerCase();
            final char[] className = new char[origName.length()];
            int index = 0;
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


                // next should be upper case.
                if (ch == '-' || ch == '_' || Character.isWhitespace(ch)) {
                    toUpperCase = true;
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
            final var javaClassName =  new String(result);
            if (abbreviation.isEmpty()) {
                return List.of(javaClassName);
            }

            return List.of(javaClassName, abbreviation);
        }

        @Override
        public String convert(final String name) {
            return convertPreserveAbbreviation(name).get(0);
        }
    }

}
