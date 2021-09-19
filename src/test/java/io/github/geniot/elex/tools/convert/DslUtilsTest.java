package io.github.geniot.elex.tools.convert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DslUtilsTest {
    @Test
    public void testTokenize() {
        String input = "\t[m3][c darkslategray][u]Thesaurus:[/u][/c] [ref dict=&quot;Macmillan English Thesaurus (En-En)&quot;]shared or divided and not shared or divided[/ref][sub][c rosybrown]synonym[/c][/sub] ";
        String[] tokens = DslUtils.tokenize(input);
        Assertions.assertEquals(17, tokens.length);
    }
}
