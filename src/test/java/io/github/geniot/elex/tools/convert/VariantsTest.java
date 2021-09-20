package io.github.geniot.elex.tools.convert;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VariantsTest {
    @Test
    public void testVariants1() {
        String key = "(the) United States\n" +
                "(the) United States of America\n" +
                "(the) United States \\(of America\\)";
        String value = "definition";
        String expected =
                        "{\"the United States\":\"(the) United States\\n(the) United States of America\\n(the) United States \\\\(of America\\\\)\\ndefinition\"," +
                        "\"the United States (of America)\":\"[ref]the United States[/ref]\"," +
                        "\"the United States of America\":\"[ref]the United States[/ref]\"}";
        Gson gson = new Gson();
        Assertions.assertEquals(expected, gson.toJson(DslDictionary.getVariants(key, value, false)));
    }
}
