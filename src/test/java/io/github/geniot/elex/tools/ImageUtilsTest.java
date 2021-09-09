package io.github.geniot.elex.tools;

import io.github.geniot.elex.tools.convert.ImageUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class ImageUtilsTest {
    @Test
    public void testBmbToPng() {
        try {
            byte[] input1 = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("icon.bmp"));
            byte[] input2 = IOUtils.toByteArray(Thread.currentThread().getContextClassLoader().getResourceAsStream("expected.png"));
            byte[] output1 = ImageUtils.bmp2png(input1);
//            FileUtils.writeByteArrayToFile(new File("data/iconOut.png"), output1);
//            Assertions.assertArrayEquals(input2, output1);
        } catch (IOException e) {
            fail(e);
        }
    }
}
