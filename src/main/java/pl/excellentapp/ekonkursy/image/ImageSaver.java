package pl.excellentapp.ekonkursy.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSaver {

    public File saveImage(BufferedImage image, String fileName) {
        File outputFile = new File("./temp/" + fileName);
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            System.err.println("Błąd zapisu obrazu: " + e.getMessage());
        }
        return outputFile;
    }
}
