package pl.excellentapp.ekonkursy.scene.effects;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ElementSize;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

import static org.bytedeco.opencv.global.opencv_imgproc.resize;

public class ResizeEffect implements SceneEffect {

    @Override
    public Mat applyEffect(SceneConfig sceneConfig, SceneElement sceneElement, Mat frame, Mat image, int currentFrame) {
        ElementPosition position = sceneElement.getPosition();
        boolean considerMargins = sceneElement.isConsiderMargins();
        SceneMargin margin = sceneConfig.getMargin();
        ElementSize size = sceneElement.getSize();
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
        maxWidth = Math.min(maxWidth, size.getMaxWidth());
        maxHeight = Math.min(maxHeight, size.getMaxHeight());

        double scaleX = (double) maxWidth / imgWidth;
        double scaleY = (double) maxHeight / imgHeight;
        double scale = Math.min(scaleX, scaleY);

        int newWidth = (int) (imgWidth * scale);
        int newHeight = (int) (imgHeight * scale);

        Mat resizedImage = new Mat();
        resize(image, resizedImage, new Size(newWidth, newHeight));

        return resizedImage;
    }
}
