package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;

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
    protected final Size size;
    protected final boolean considerMargins;
    protected final boolean applyAlphaBlending;

    public SceneElement(ElementPosition position, int displayDuration, int delay, int fps, Size size, boolean considerMargins) {
        this.position = position;
        this.frameStart = delay * fps;
        this.frameEnd = this.frameStart + (displayDuration * fps);
        this.size = size;
        this.considerMargins = considerMargins;
        this.applyAlphaBlending = false;
    }

    public SceneElement(ElementPosition position, int displayDuration, int delay, int fps, Size size, boolean considerMargins, boolean applyAlphaBlending) {
        this.position = position;
        this.frameStart = delay * fps;
        this.frameEnd = this.frameStart + (displayDuration * fps);
        this.size = size;
        this.considerMargins = considerMargins;
        this.applyAlphaBlending = applyAlphaBlending;
    }

    public abstract void render(SceneMargin margin, Mat frame, int currentFrame);

    protected void addToVideoFrame(SceneMargin margin, Mat frame, Mat image) {
        Mat resizedImage = getResizedImage(margin, image, frame);
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

    private Mat getResizedImage(SceneMargin margin, Mat image, Mat frame) {
        int frameWidth = frame.cols();
        int frameHeight = frame.rows();

        int imgWidth = image.cols();
        int imgHeight = image.rows();

        int availableLeft = position.getLeft();
        int availableRight = frameWidth - position.getLeft();
        int availableTop = position.getTop();
        int availableBottom = frameHeight - position.getTop();

        int maxWidth = Math.min(availableLeft, availableRight) * 2;
        int maxHeight = Math.min(availableTop, availableBottom) * 2;
        if (considerMargins) {
            maxWidth = Math.min(availableLeft - margin.getLeft(), availableRight - margin.getRight()) * 2;
            maxHeight = Math.min(availableTop - margin.getTop(), availableBottom - margin.getBottom()) * 2;
        }
        maxWidth = Math.min(maxWidth, size.width());
        maxHeight = Math.min(maxHeight, size.height());

        double scaleX = (double) maxWidth / imgWidth;
        double scaleY = (double) maxHeight / imgHeight;
        double scale = Math.min(scaleX, scaleY);

        int newWidth = (int) (imgWidth * scale);
        int newHeight = (int) (imgHeight * scale);

        Mat resizedImage = new Mat();
        resize(image, resizedImage, new Size(newWidth, newHeight));

        return resizedImage;
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
