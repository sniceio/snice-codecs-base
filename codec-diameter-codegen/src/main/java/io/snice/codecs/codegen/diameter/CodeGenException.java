/**
 *
 */
package io.snice.codecs.codegen.diameter;


/**
 * @author jonas@jonasborjesson.com
 */
public class CodeGenException extends RuntimeException {

    public CodeGenException(final String message, final Exception cause) {
        super(message, cause);
    }

    public CodeGenException(final String message) {
        super(message);
    }

}
