package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.core.CvType;
import pl.excellentapp.ekonkursy.core.FontLoader;
import pl.excellentapp.ekonkursy.core.TextRenderer;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

@Getter
public class TextElement extends SceneElement {

    private final String text;
    private final int fontSize;
    private final Color color;
    private final byte[] pixels;

    public TextElement(String text, ElementPosition position, int displayDuration, int delay, int fontSize, Color color, int fps, Size size, boolean considerMargins) {
        super(position, displayDuration, delay, fps, size, considerMargins, true);
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
        this.pixels = preparePixels();
    }

    @Override
    public void render(SceneMargin margin, Mat frame, int currentFrame) {
        if (currentFrame < frameStart || currentFrame > frameEnd) {
            return;
        }
        Mat image = new Mat(size.height(), size.width(), CvType.CV_8UC4);
        image.data().put(pixels);

        addToVideoFrame(margin, frame, image);
    }

    private byte[] preparePixels() {
        FontLoader fontLoader = new FontLoader();
        TextRenderer textRenderer = new TextRenderer(fontLoader);
        int width = size.width();
        int height = size.height();
        BufferedImage transparentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = transparentImage.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcOver);
        textRenderer.drawText(g2d, text, width, height, color);
        g2d.dispose();
        BufferedImage byteImage = convertToByteBufferedImage(transparentImage);
        return ((DataBufferByte) byteImage.getRaster().getDataBuffer()).getData();
    }

    private BufferedImage convertToByteBufferedImage(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

}
