package io.github.geniot.elex.tools.convert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HtmlUtilsTest {
    @Test
    public void testToHtmlNew1() {
        String article = "[m1][p]adjective[/p]";
        String expected = "<span class=\"p\">adjective</span><br/>\n";
        String result = HtmlUtils.toHtml("", "", false, null, article);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testToHtmlNew2() {
        String article = "[m2][p]adjective[/p]";
        String expected = "<span class=\"m2\"><span class=\"p\">adjective</span></span><br/>\n";
        String result = HtmlUtils.toHtml("", "", false, null, article);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void testToHtmlNew3() {
        String article = "\t[m2][c darkslateblue]BrE[/c] [c gray]/[/c][c navy]ˈɡɪftɪd[/c][c gray]/[/c] [s]gifted__gb_1.wav[/s]; [c indianred]NAmE[/c] [c gray]/[/c][c navy]ˈɡɪftɪd[/c][c gray]/[/c] [s]gifted__us_1.wav[/s]";
        String expected = "<span class=\"m2\">\t<span class=\"c\">BrE</span> <span class=\"c\">/</span><span class=\"c\">ˈɡɪftɪd</span><span class=\"c\">/</span> <span class=\"s\">gifted__gb_1.wav</span>; <span class=\"c\">NAmE</span> <span class=\"c\">/</span><span class=\"c\">ˈɡɪftɪd</span><span class=\"c\">/</span> <span class=\"s\">gifted__us_1.wav</span></span><br/>\n";
        String result = HtmlUtils.toHtml("", "", false, null, article);
        Assertions.assertEquals(expected, result);
    }
}
