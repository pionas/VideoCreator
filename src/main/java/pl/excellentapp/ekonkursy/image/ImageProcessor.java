package pl.excellentapp.ekonkursy.image;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.file.Path;

public class ImageProcessor {

    public void applyBackground(Path imagePath, Color backgroundColor) {
        try {
            BufferedImage original = ImageIO.read(imagePath.toFile());
            BufferedImage newImage = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = newImage.createGraphics();
            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, original.getWidth(), original.getHeight());
            g2d.drawImage(original, 0, 0, null);
            g2d.dispose();
            ImageIO.write(newImage, "jpg", imagePath.toFile());
        } catch (Exception ignored) {
        }
    }
}
