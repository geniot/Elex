package io.github.geniot.elex.tools.convert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class HtmlUtilsTest {
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
}
