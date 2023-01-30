package io.github.geniot.elex.tools.convert;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VariantsTest {
    @Test
    public void testSimple() {
        String key = "'Arry";
        String value = "\t\\[[t]'ærɪ[/t]\\]";
        String expected =
                "{\"\\u0027Arry\":\"\\t\\\\[[t]\\u0027ærɪ[/t]\\\\]\"}";
        Gson gson = new Gson();
        Assertions.assertEquals(expected, gson.toJson(DslDictionary.getVariants(key, value)));
    }

    @Test
    public void testBrackets() {
        String key = "(')cause of{ }";
        String value = "\t\\[[t]'kɔzəv[/t]\\]";
        String expected =
                "{\"\\u0027cause of\":" +
                        "\"\\t[h](\\u0027)cause of[/h]\\n\\t\\\\[[t]\\u0027kɔzəv[/t]\\\\]\"}";
        Gson gson = new Gson();
        Assertions.assertEquals(expected, gson.toJson(DslDictionary.getVariants(key, value)));
    }

    @Test
    public void testBracketsSimple() {
        String key = "(')cuz of";
        String value = "\t\\[[t]'kɔzəv[/t]\\]";
        String expected =
                "{\"\\u0027cuz of\":" +
                        "\"\\t[h](\\u0027)cuz of[/h]\\n\\t\\\\[[t]\\u0027kɔzəv[/t]\\\\]\"}";
        Gson gson = new Gson();
        Assertions.assertEquals(expected, gson.toJson(DslDictionary.getVariants(key, value)));
    }

    @Test
    public void testMultiple() {
        String key = "a good deal of\n" +
                "a great deal of\n" +
                "a vast deal of";
        String value = "\t[m1][trn]много[/trn][/m]";
        String expected =
                "{\"a good deal of\":" +
                        "\"\\t[h]a good deal of[/h]\\n\\t[h]a great deal of[/h]\\n\\t[h]a vast deal of[/h]\\n\\t[m1][trn]много[/trn][/m]\"," +
                        "\"a great deal of\":\"\\t[ref]a good deal of[/ref]\"," +
                        "\"a vast deal of\":\"\\t[ref]a good deal of[/ref]\"}";
        Gson gson = new Gson();
        Assertions.assertEquals(expected, gson.toJson(DslDictionary.getVariants(key, value)));
    }

    @Test
    public void testMultiBrackets() {
        String key = "(the) United States\n" +
                "(the) United States of America\n" +
                "(the) United States \\(of America\\)";
        String value = "definition";
        String expected =
                "{\"the United States\":" +
                        "\"\\t[h](the) United States[/h]\\n\\t[h](the) United States of America[/h]\\n\\t[h](the) United States \\\\(of America\\\\)[/h]\\ndefinition\"," +
                        "\"the United States \\\\(of America\\\\)\":\"\\t[ref]the United States[/ref]\"," +
                        "\"the United States of America\":\"\\t[ref]the United States[/ref]\"}";
        Gson gson = new Gson();
        Assertions.assertEquals(expected, gson.toJson(DslDictionary.getVariants(key, value)));
    }

    @Test
    public void testCurly() {
        String key = "{to }beggar belief\n" +
                "{to }beggar description\n" +
                "{to }beggar the imagination";
        String value = "definition";
        String expected =
                "{\"beggar belief\":" +
                        "\"\\t[h]to beggar belief[/h]\\n\\t[h]to beggar description[/h]\\n\\t[h]to beggar the imagination[/h]\\ndefinition\"," +
                        "\"beggar description\":\"\\t[ref]beggar belief[/ref]\"," +
                        "\"beggar the imagination\":\"\\t[ref]beggar belief[/ref]\"}";
        Gson gson = new Gson();
        Assertions.assertEquals(expected, gson.toJson(DslDictionary.getVariants(key, value)));
    }
}
