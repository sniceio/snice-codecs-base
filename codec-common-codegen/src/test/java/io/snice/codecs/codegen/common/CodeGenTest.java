package io.snice.codecs.codegen.common;

import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Path;

public class CodeGenTest {

    /**
     * This is really not a test. Just a quick way to see what's being generated
     * @throws Exception
     */
    @Ignore
    @Test
    public void testGenerateE212() throws Exception {
        final var output = Path.of(".");
        final var codeGen = new CodeGen();
        codeGen.execute(output);
    }
}
