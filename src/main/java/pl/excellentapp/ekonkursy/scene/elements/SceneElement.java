package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.effects.ResizeEffect;
import pl.excellentapp.ekonkursy.scene.effects.SceneEffect;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.add;
import static org.bytedeco.opencv.global.opencv_core.bitwise_and;
import static org.bytedeco.opencv.global.opencv_core.bitwise_not;
import static org.bytedeco.opencv.global.opencv_core.merge;
import static org.bytedeco.opencv.global.opencv_core.split;
import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGRA2BGR;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_imgproc.resize;

@Getter
public abstract class SceneElement {

    protected final ElementPosition position;
    protected final int frameStart;
    protected final int frameEnd;
    protected final ElementSize size;
    protected final List<SceneEffect> effects = new ArrayList<>();
    protected final boolean considerMargins;
    protected final boolean applyAlphaBlending;

    public SceneElement(ElementPosition position, double displayDuration, double delay, int fps, ElementSize size, boolean considerMargins) {
        this.position = position;
        this.frameStart = (int) (delay * fps);
        this.frameEnd = (int) (this.frameStart + (displayDuration * fps));
        this.size = size;
        this.considerMargins = considerMargins;
        this.applyAlphaBlending = false;
        this.effects.add(new ResizeEffect());
    }

    public SceneElement(ElementPosition position, double displayDuration, double delay, int fps, ElementSize size, boolean considerMargins, boolean applyAlphaBlending) {
        this.position = position;
        this.frameStart = (int) (delay * fps);
        this.frameEnd = (int) (this.frameStart + (displayDuration * fps));
        this.size = size;
        this.considerMargins = considerMargins;
        this.applyAlphaBlending = applyAlphaBlending;
        this.effects.add(new ResizeEffect());
    }

    public SceneElement(ElementPosition position, double displayDuration, double delay, int fps, ElementSize size, boolean considerMargins, List<SceneEffect> effects) {
        this.position = position;
        this.frameStart = (int) (delay * fps);
        this.frameEnd = (int) (this.frameStart + (displayDuration * fps));
        this.size = size;
        this.considerMargins = considerMargins;
        this.applyAlphaBlending = false;
        this.effects.addAll(effects);
    }

    public SceneElement(ElementPosition position, double displayDuration, double delay, int fps, ElementSize size, boolean considerMargins, boolean applyAlphaBlending, List<SceneEffect> effects) {
        this.position = position;
        this.frameStart = (int) (delay * fps);
        this.frameEnd = (int) (this.frameStart + (displayDuration * fps));
        this.size = size;
        this.considerMargins = considerMargins;
        this.applyAlphaBlending = applyAlphaBlending;
        this.effects.addAll(effects);
    }

    public abstract void render(SceneConfig sceneConfig, Mat frame, int currentFrame);

    protected void addToVideoFrame(SceneConfig sceneConfig, Mat frame, Mat image, int currentFrame) {
        Mat resizedImage = image.clone();
        for (SceneEffect effect : effects) {
            Mat newImage = effect.applyEffect(sceneConfig, this, frame, resizedImage, currentFrame);
            resizedImage.release();
            resizedImage = newImage;
        }
        try {
            int left = position.getLeft() - (resizedImage.cols() / 2);
            int top = position.getTop() - (resizedImage.rows() / 2);
            Rect roi = new Rect(left, top, resizedImage.cols(), resizedImage.rows());
            Mat submat = frame.apply(roi);
            if (applyAlphaBlending) {
                applyAlphaBlending(resizedImage, submat);
            } else {
                resizedImage.copyTo(submat);
            }
        } catch (Exception e) {
            System.err.println("Błąd kopiowania obrazu do ramki: " + e.getMessage());
        } finally {
            image.release();
            if (resizedImage != image) {
                resizedImage.release();
            }
        }
    }

    private void applyAlphaBlending(Mat resizedImage, Mat submat) {
        MatVector channels = new MatVector(4);
        if (channels.size() < 4) {
            resizedImage.copyTo(submat);
            return;
        }
        split(resizedImage, channels);
        Mat alpha = channels.get(3);
        if (alpha.size().width() != submat.size().width() || alpha.size().height() != submat.size().height()) {
            resize(alpha, alpha, new Size(submat.size().width(), submat.size().height()));
        }
        Mat invertedAlpha = new Mat();
        bitwise_not(alpha, invertedAlpha);

        Mat background = new Mat();
        submat.copyTo(background);

        if (background.channels() != 3) {
            cvtColor(background, background, COLOR_BGRA2BGR);
        }

        Mat foreground = new Mat();
        cvtColor(resizedImage, foreground, COLOR_BGRA2BGR);

        MatVector bgChannels = new MatVector(3);
        MatVector fgChannels = new MatVector(3);
        split(background, bgChannels);
        split(foreground, fgChannels);

        for (int i = 0; i < 3; i++) {
            bitwise_and(bgChannels.get(i), invertedAlpha, bgChannels.get(i));
            bitwise_and(fgChannels.get(i), alpha, fgChannels.get(i));
            add(bgChannels.get(i), fgChannels.get(i), bgChannels.get(i));
        }
        merge(bgChannels, background);
        background.copyTo(submat);
    }
}
