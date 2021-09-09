package io.github.geniot.elex.tools;

import io.github.geniot.elex.tools.convert.DslLine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class DslLineTest {
    @Test
    public void testLine() {
        try {
            String line1 = "";
            String line2 = "";
            System.out.println(new DslLine(line1));
        } catch (Exception e) {
            fail(e);
        }

    }
}
