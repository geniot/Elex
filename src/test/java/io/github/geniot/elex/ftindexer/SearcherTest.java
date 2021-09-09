package io.github.geniot.elex.ftindexer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.fail;

public class SearcherTest {

    @Test
    public void testSearch() {
        try {
            int hitsPerPage = 10;
            FtServer.getInstance();
            long t1 = System.currentTimeMillis();
            SortedMap<Float, String[]> result = FtServer.getInstance().search("Longman_DOCE5.ezp", "test", hitsPerPage);
            Assertions.assertTrue(result.size() > 0);
            long t2 = System.currentTimeMillis();
            System.out.println("Search took " + (t2 - t1) + " ms");
            result = FtServer.getInstance().search("Longman_DOCE5.ezp", "more", hitsPerPage);
            Assertions.assertTrue(result.size() > 0);
            long t3 = System.currentTimeMillis();
            System.out.println("Next search took " + (t3 - t2) + " ms");

            result = FtServer.getInstance().search("Longman_DOCE5.ezp", "labyrinth", hitsPerPage);

            for (Float key : result.keySet()) {
                String[] value = result.get(key);
                System.out.println(key + " => " + value[0] + " : " + value[1]);
            }

            FtServer.getInstance().stop();
        } catch (IOException e) {
            fail(e);
        }
    }
}
