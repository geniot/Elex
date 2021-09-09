package io.github.geniot.elex.tools.convert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static byte[] bmp2png(byte[] pngBbs) throws IOException {
        BufferedImage inputImage = ImageIO.read(new ByteArrayInputStream(pngBbs));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(inputImage, "PNG", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
