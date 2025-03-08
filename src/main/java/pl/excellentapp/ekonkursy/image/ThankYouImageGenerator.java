package pl.excellentapp.ekonkursy.image;

import pl.excellentapp.ekonkursy.core.ColorPalette;
import pl.excellentapp.ekonkursy.core.FontLoader;
import pl.excellentapp.ekonkursy.core.TextRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Random;
import java.util.Set;

public class ThankYouImageGenerator {

    private final int width;
    private final int height;
    private final Set<String> names;
    private final Color backgroundColor;
    private final Color textColor;
    private final FontLoader fontLoader;
    private final TextRenderer textRenderer;
    private final ImageSaver imageSaver;

    private static final Random RANDOM = new Random();

    public ThankYouImageGenerator(Set<String> names, int width, int height) {
        this.width = width;
        this.height = height;
        this.names = names;
        Color[] selectedColors = ColorPalette.TEXT_COLORS.get(RANDOM.nextInt(ColorPalette.TEXT_COLORS.size()));
        this.backgroundColor = selectedColors[1];
        this.textColor = selectedColors[0];
        this.fontLoader = new FontLoader();
        this.textRenderer = new TextRenderer(fontLoader);
        this.imageSaver = new ImageSaver();
    }

    public Path generateThankYouImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        setupGraphics(g);
        drawBackground(g);
        textRenderer.drawText(g, "Podziękowania dla:", width / 2, height / 5, textColor);
        textRenderer.drawNames(g, names, width, height - 400, textColor);
        g.dispose();
        return imageSaver.saveImage(image, "thank_you.png");
    }

    private void setupGraphics(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    }

    private void drawBackground(Graphics2D g) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);
    }
}