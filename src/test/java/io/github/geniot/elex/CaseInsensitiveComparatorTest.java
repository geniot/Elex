package io.github.geniot.elex;

import org.junit.jupiter.api.Test;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CaseInsensitiveComparatorTest {
    @Test
    public void testComparatorSimple() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("abc");
        set.add("back");
        set.add("cool");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"abc", "back", "cool"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testComparatorSensitive() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("Abc");
        set.add("abC");
        set.add("abc");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"Abc", "abC", "abc"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testComparatorEn() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("abc");
        set.add("Acc");
        set.add("aCc");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"abc", "Acc", "aCc"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testComparatorReal() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("e-billing");
        set.add("E-boat");
        set.add("e-book");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"e-billing", "E-boat", "e-book"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testDiacritic() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("pagina");
        set.add("página");
        set.add("pahina");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"pagina", "página", "pahina"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testRu() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("Родина");
        set.add("роДина");
        set.add("родина");
        set.add("родинка");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"Родина", "роДина", "родина", "родинка"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testRu2() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("аБв");
        set.add("абВ");
        set.add("абв");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"аБв", "абВ", "абв"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testRu3() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("абажур");
        set.add("Авиньон");
        set.add("автокар");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"абажур", "Авиньон", "автокар"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testRu4() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("а");
        set.add("а именно");
        set.add("абажур");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"а", "а именно", "абажур"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testRu5() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("oIN-код");
        set.add("PIN-код");
        set.add("а");
        set.add("а именно");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"oIN-код", "PIN-код", "а", "а именно"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testEs() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("n");
        set.add("ñ");
        set.add("nа");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"n", "ñ", "nа"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testRu6() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparatorV4());
        set.add("-test");
        set.add("-значный");
        set.add("CD-плеер");
        set.add("а");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"-test", "-значный", "CD-плеер", "а"};
        assertArrayEquals(expected, values);
    }
}
