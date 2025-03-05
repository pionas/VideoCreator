package pl.excellentapp.ekonkursy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Set;

import static pl.excellentapp.ekonkursy.VideoCreatorFacade.IMAGE_DIRECTORY;

public class ThankYouScreenGenerator {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    private static final String FONT_TITLE_PATH = "./fonts/BebasNeue-Regular.ttf";
    private static final String FONT_NAMES_PATH = "./fonts/Lato-Regular.ttf";
    private static final String FONT_NAMES_BOLD_PATH = "./fonts/Lato-Bold.ttf";

    private static final int TITLE_FONT_SIZE = 80;
    private static final int NAMES_FONT_SIZE = 40;
    public static final Random RANDOM = new Random();

    private Color backgroundColor;
    private Color textColor;

    public File generateThankYouScreen(Set<String> names) {
        Color[] selectedColors = TextColorConfig.TEXT_COLORS.get(RANDOM.nextInt(TextColorConfig.TEXT_COLORS.size()));
        boolean firstColorForThanksMessage = RANDOM.nextBoolean();
        this.backgroundColor = firstColorForThanksMessage ? selectedColors[0] : selectedColors[1];
        this.textColor = firstColorForThanksMessage ? selectedColors[1] : selectedColors[0];

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        drawBackground(g);

        Font titleFont = loadCustomFont(FONT_TITLE_PATH, TITLE_FONT_SIZE);

        g.setFont(titleFont);
        g.setColor(textColor);
        drawCenteredText(g, "Podziękowania dla:", WIDTH / 2, 120);

        drawNames(g, names);

        g.dispose();

        String outputPath = String.format("./%s/thank_you.png", IMAGE_DIRECTORY);
        File outputFile = new File(outputPath);
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            System.err.println("Błąd zapisu obrazu: " + e.getMessage());
        }

        return outputFile;
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, WIDTH, HEIGHT);
    }

    private void drawNames(Graphics2D g, Set<String> names) {
        Font nameFont = loadCustomFont(FONT_NAMES_PATH, NAMES_FONT_SIZE);
        Font nameFontBold = loadCustomFont(FONT_NAMES_BOLD_PATH, NAMES_FONT_SIZE);

        boolean[][] occupiedPixels = new boolean[WIDTH][HEIGHT]; // Mapa zajętych pikseli
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        double angle = 0;
        double radius = 0;
        double angleStep = Math.PI / 6;
        double radiusStep = 30;

        int buffer = 10; // Zapewnia dodatkowy margines wokół tekstu

        for (String name : names) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 500) {
                int x = (int) (centerX + radius * Math.cos(angle));
                int y = (int) (centerY + radius * Math.sin(angle));

                g.setFont(RANDOM.nextBoolean() ? nameFont : nameFontBold);
                g.setColor(textColor);

                FontMetrics metrics = g.getFontMetrics();
                int textWidth = metrics.stringWidth(name);
                int textHeight = metrics.getHeight();

                int startX = x - textWidth / 2 - buffer;
                int startY = y - textHeight - buffer;
                int endX = x + textWidth / 2 + buffer;
                int endY = y + buffer;

                if (isAreaFree(occupiedPixels, startX, startY, endX, endY)) {
                    drawRotatedText(g, name, x, y);
                    markAreaAsOccupied(occupiedPixels, startX, startY, endX, endY);
                    placed = true;
                }

                angle += angleStep;
                radius += radiusStep / angle;
                attempts++;
            }
        }
    }

    private boolean isAreaFree(boolean[][] occupiedPixels, int startX, int startY, int endX, int endY) {
        for (int i = Math.max(0, startX); i < Math.min(WIDTH, endX); i++) {
            for (int j = Math.max(0, startY); j < Math.min(HEIGHT, endY); j++) {
                if (occupiedPixels[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void markAreaAsOccupied(boolean[][] occupiedPixels, int startX, int startY, int endX, int endY) {
        for (int i = Math.max(0, startX); i < Math.min(WIDTH, endX); i++) {
            for (int j = Math.max(0, startY); j < Math.min(HEIGHT, endY); j++) {
                occupiedPixels[i][j] = true;
            }
        }
    }

    private void drawRotatedText(Graphics2D g, String text, int x, int y) {
        double rotation = (RANDOM.nextDouble() - 0.5) * Math.PI / 8;
        AffineTransform originalTransform = g.getTransform();
        g.rotate(rotation, x, y);
        g.drawString(text, x, y);
        g.setTransform(originalTransform);
    }

    private void drawCenteredText(Graphics2D g, String text, int x, int y) {
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        g.drawString(text, x - textWidth / 2, y);
    }

    private Font loadCustomFont(String fontPath, int size) {
        try {
            File fontFile = new File(fontPath);
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont((float) size);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font;
        } catch (Exception e) {
            System.err.println("Błąd ładowania czcionki: " + e.getMessage());
            return new Font("Arial", Font.BOLD, size);
        }
    }
}
