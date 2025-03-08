package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;

@Getter
public abstract class SceneElement {

    protected final ElementPosition position;
    protected final int frameStart;
    protected final int frameEnd;
    protected final Size size;

    public SceneElement(ElementPosition position, int displayDuration, int delay, int fps, Size size) {
        this.position = position;
        this.frameStart = delay * fps;
        this.frameEnd = this.frameStart + (displayDuration * fps);
        this.size = size;
    }

    public abstract void render(Mat frame, int currentFrame);

    protected void addToVideoFrame(Mat frame, Mat image) {
        Mat resizedImage = getResizedImage(image, frame);
        try {
            int left = position.getLeft() - (resizedImage.cols() / 2);
            int top = position.getTop() - (resizedImage.rows() / 2);
            Rect roi = new Rect(left, top, resizedImage.cols(), resizedImage.rows());
            Mat submat = frame.apply(roi);
            resizedImage.copyTo(submat);
        } catch (Exception e) {
            System.err.println("Błąd kopiowania obrazu do ramki: " + e.getMessage());
        } finally {
            image.release();
            if (resizedImage != image) {
                resizedImage.release();
            }
        }
    }

    private Mat getResizedImage(Mat image, Mat frame) {
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

        double scaleX = (double) maxWidth / imgWidth;
        double scaleY = (double) maxHeight / imgHeight;
        double scale = Math.min(scaleX, scaleY);

        int newWidth = (int) (imgWidth * scale);
        int newHeight = (int) (imgHeight * scale);

        Mat resizedImage = new Mat();
        opencv_imgproc.resize(image, resizedImage, new Size(newWidth, newHeight));

        return resizedImage;
    }

}
