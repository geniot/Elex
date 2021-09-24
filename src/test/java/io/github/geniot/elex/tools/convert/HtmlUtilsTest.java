package io.github.geniot.elex.tools.convert;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.fail;

public class HtmlUtilsTest {

    static String article;

    @BeforeAll
    static public void beforeAll() {
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("testArticle.txt");
            article = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    public void testToHtmlNew1() {
        String article = "[m1][p]adjective[/p]";
        String expected = "<span class=\"p\">adjective</span><br/>\n";
        String result = HtmlUtils.toHtml("", "", false, null, article, new Properties());
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testToHtmlNew2() {
        String article = "[m2][p]adjective[/p]";
        String expected = "<span class=\"m2\"><span class=\"p\">adjective</span></span><br/>\n";
        String result = HtmlUtils.toHtml("", "", false, null, article, new Properties());
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testToHtmlNew3() {
        String article = "\t[m2][c darkslateblue]BrE[/c] [c gray]/[/c][c navy]ˈɡɪftɪd[/c][c gray]/[/c] [s]gifted__gb_1.wav[/s]; [c indianred]NAmE[/c] [c gray]/[/c][c navy]ˈɡɪftɪd[/c][c gray]/[/c] [s]gifted__us_1.wav[/s]";
        String expected = "<span class=\"m2\">\t<span style=\"color:darkslateblue\">BrE</span> <span style=\"color:gray\">/</span><span style=\"color:navy\">ˈɡɪftɪd</span><span style=\"color:gray\">/</span> <span class=\"sound\" data-id=\"\" data-link=\"gifted__gb_1.wav\"><span style=\"display:none\">gifted__gb_1.wav</span></span>; <span style=\"color:indianred\">NAmE</span> <span style=\"color:gray\">/</span><span style=\"color:navy\">ˈɡɪftɪd</span><span style=\"color:gray\">/</span> <span class=\"sound\" data-id=\"\" data-link=\"gifted__us_1.wav\"><span style=\"display:none\">gifted__us_1.wav</span></span></span><br/>\n";
        String result = HtmlUtils.toHtml("", "", false, null, article, new Properties());
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testToHtmlPerformance() {
        try {
            long t1 = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                HtmlUtils.toHtml("", "", false, null, article, new Properties());
            }
            long t2 = System.currentTimeMillis();
            System.out.println(t2 - t1);
            Assertions.assertEquals(true, t2 - t1 < 200);
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    public void testTagName() {
        String tagName = DslUtils.tagName("[some tag]");
        Assertions.assertEquals("some", tagName);
        tagName = DslUtils.tagName("[some]");
        Assertions.assertEquals("some", tagName);
        tagName = DslUtils.tagName("[/some]");
        Assertions.assertEquals("some", tagName);
    }

    @Test
    public void testTokenize() {
        String[] tokens = DslUtils.tokenize(article);
        Assertions.assertEquals(1491, tokens.length);
        //
        tokens = DslUtils.tokenize("some");
        Assertions.assertEquals(1, tokens.length);
        Assertions.assertEquals("some", tokens[0]);
    }
}
