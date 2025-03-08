package pl.excellentapp.ekonkursy.image;

import pl.excellentapp.ekonkursy.core.ProjectProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageSaver {

    public Path saveImage(BufferedImage image, String fileName) {
        Path outputFile = ProjectProperties.TEMPORARY_DIRECTORY.resolve(fileName);

        try {
            Files.createDirectories(ProjectProperties.TEMPORARY_DIRECTORY);
            ImageIO.write(image, "png", outputFile.toFile());
        } catch (IOException e) {
            System.err.println("Błąd zapisu obrazu: " + e.getMessage());
        }
        return outputFile;
    }
}
