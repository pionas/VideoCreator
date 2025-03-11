package pl.excellentapp.ekonkursy.image;

import pl.excellentapp.ekonkursy.config.ProjectProperties;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ImageStripGenerator {

    private static final int BORDER_SIZE = 20;
    private static final int PADDING = 10;
    private static final int IMAGE_MARGIN = 20;
    private static final int PERFORATION_WIDTH = 15;
    private static final int PERFORATION_HEIGHT = 30;
    private static final int PERFORATION_SPACING = 40;
    private static final Color PERFORATION_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(0, 0, 0);

    public void createFilmStrip(String outputFilePath) throws IOException {
        List<String> imagePaths = getImagePaths();
        if (imagePaths.isEmpty()) {
            throw new IllegalArgumentException("No images provided");
        }
        int width = ProjectProperties.VideoSettings.WIDTH;
        int totalHeight = (imagePaths.size() - 1) * (width + PADDING);

        BufferedImage filmStrip = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = filmStrip.createGraphics();

        g.setColor(PERFORATION_COLOR);
        g.fillRect(0, 0, width, totalHeight);

        g.setColor(BORDER_COLOR);
        g.fillRect(0, 0, width, IMAGE_MARGIN);

        int yOffset = BORDER_SIZE;
        for (String path : imagePaths) {
            BufferedImage img = ImageIO.read(new File(path));

            int newWidth = width;
            int newHeight = width;

            double aspectRatio = (double) img.getWidth() / img.getHeight();

            if (img.getWidth() > img.getHeight()) {
                newHeight = (int) (width / aspectRatio);
            } else {
                newWidth = (int) (width * aspectRatio);
            }

            Image scaledImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setColor(PERFORATION_COLOR);
            g2d.fillRect(0, 0, width, width);

            int xPos = (width - newWidth) / 2;
            int yPos = (width - newHeight) / 2;
            g2d.drawImage(scaledImage, xPos, yPos, null);
            g2d.dispose();

            g.drawImage(resizedImage, BORDER_SIZE + PERFORATION_WIDTH, yOffset, null);

            g.setColor(BORDER_COLOR);
            g.fillRect(0, yOffset + width, width, IMAGE_MARGIN);

            yOffset += width + IMAGE_MARGIN;
        }

        g.setColor(BORDER_COLOR);
        g.fillRect(0, 0, BORDER_SIZE + PERFORATION_WIDTH, totalHeight);
        g.fillRect(width - PERFORATION_WIDTH - BORDER_SIZE, 0, PERFORATION_WIDTH + BORDER_SIZE, totalHeight);

        g.setColor(PERFORATION_COLOR);
        for (int y = BORDER_SIZE; y < totalHeight - BORDER_SIZE; y += PERFORATION_SPACING) {
            g.fillRect(BORDER_SIZE / 2, y, PERFORATION_WIDTH, PERFORATION_HEIGHT);
            g.fillRect(width - PERFORATION_WIDTH - (BORDER_SIZE / 2), y, PERFORATION_WIDTH, PERFORATION_HEIGHT);
        }

        g.dispose();
        ImageIO.write(filmStrip, "jpg", new File(outputFilePath));
    }

    private List<String> getImagePaths() {
        File imageDir = new File(ProjectProperties.TEMPORARY_DIRECTORY.toUri());
        return Stream.of(Objects.requireNonNull(imageDir.listFiles()))
                .map(File::getAbsolutePath)
                .toList();
    }

}
