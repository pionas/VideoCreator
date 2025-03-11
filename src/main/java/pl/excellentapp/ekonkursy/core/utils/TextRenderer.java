package pl.excellentapp.ekonkursy.core.utils;

import lombok.RequiredArgsConstructor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public class TextRenderer {

    private static final Random RANDOM = new Random();
    private final Font titleFont;
    private final Font nameFont;
    private final Font nameFontBold;

    public TextRenderer(FontLoader fontLoader) {
        this.titleFont = fontLoader.loadFont("./fonts/BebasNeue-Regular.ttf", 80);
        this.nameFont = fontLoader.loadFont("./fonts/Lato-Regular.ttf", 40);
        this.nameFontBold = fontLoader.loadFont("./fonts/Lato-Bold.ttf", 40);
    }

    public void drawText(Graphics2D g, String text, int x, int y, Color color) {
        g.setFont(titleFont);
        g.setColor(color);
        drawCenteredText(g, text, x, y);
    }

    public void drawNames(Graphics2D g, Set<String> names, int width, int height, Color color) {
        Set<Rectangle> occupiedAreas = new HashSet<>();
        int centerX = width / 2;
        int centerY = height / 2;
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

                BufferedImage textImage = renderTextWithRotation(name, fontToUse, color, rotationAngle);
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

    private void drawCenteredText(Graphics2D g, String text, int width, int height) {
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + metrics.getAscent();
        g.drawString(text, x, y);
    }
}
