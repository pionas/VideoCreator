package pl.excellentapp.ekonkursy;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
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
        Set<Rectangle> occupiedAreas = new HashSet<>();

        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        double angle = 0;
        double radius = 0;
        double angleStep = Math.PI / 6;
        double radiusStep = 30;
        int buffer = 15;

        for (String name : names) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 500) {
                int x = (int) (centerX + radius * Math.cos(angle));
                int y = (int) (centerY + radius * Math.sin(angle));

                Font fontToUse = RANDOM.nextBoolean() ? nameFont : nameFontBold;
                double rotationAngle = (RANDOM.nextDouble() - 0.5) * Math.PI / 4;

                BufferedImage textImage = renderTextWithRotation(name, fontToUse, textColor, rotationAngle);
                int textWidth = textImage.getWidth();
                int textHeight = textImage.getHeight();

                Rectangle textRect = new Rectangle(
                        x - textWidth / 2 - buffer,
                        y - textHeight / 2 - buffer,
                        textWidth + buffer * 2,
                        textHeight + buffer * 2
                );

                boolean collision = occupiedAreas.stream().anyMatch(existing -> existing.intersects(textRect));

                if (!collision) {
                    g.drawImage(textImage, x - textWidth / 2, y - textHeight / 2, null);
                    occupiedAreas.add(textRect);
                    placed = true;
                }

                angle += angleStep;
                radius += radiusStep / angle;
                attempts++;
            }
        }
    }

    private BufferedImage renderTextWithRotation(String text, Font font, Color color, double rotationAngle) {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();

        int imgSize = (int) Math.sqrt(width * width + height * height);
        BufferedImage textImage = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB);
        g2d = textImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        g2d.setFont(font);
        g2d.setColor(color);

        int centerX = imgSize / 2;
        int centerY = imgSize / 2;

        g2d.translate(centerX, centerY);
        g2d.rotate(rotationAngle);
        g2d.drawString(text, -width / 2, fm.getAscent() - height / 2);
        g2d.dispose();

        return textImage;
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
