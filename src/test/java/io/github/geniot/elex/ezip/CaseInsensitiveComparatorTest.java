package io.github.geniot.elex.ezip;

import io.github.geniot.elex.ezip.model.CaseInsensitiveComparator;
import org.junit.jupiter.api.Test;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CaseInsensitiveComparatorTest {
    @Test
    public void testComparatorSimple() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparator());
        set.add("abc");
        set.add("back");
        set.add("cool");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"abc", "back", "cool"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testComparatorSensitive() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparator());
        set.add("abc");
        set.add("Abc");
        set.add("abC");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"abc", "abC", "Abc"};
        assertArrayEquals(expected, values);
    }

    @Test
    public void testComparatorReal() {
        SortedSet<String> set = new TreeSet<>(new CaseInsensitiveComparator());
        set.add("e-billing");
        set.add("E-boat");
        set.add("e-book");
        String[] values = set.toArray(new String[set.size()]);
        String[] expected = new String[]{"e-billing", "E-boat", "e-book"};
        assertArrayEquals(expected, values);
    }
}
