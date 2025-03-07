package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

import java.io.File;

@Getter
public class ImageElement extends SceneElement {

    private final File filePath;
    private final boolean keepAfterEnd;

    public ImageElement(File filePath, ElementPosition position, int displayDuration, int delay, int fps, boolean keepAfterEnd) {
        super(position, displayDuration, delay, fps);
        this.filePath = filePath;
        this.keepAfterEnd = keepAfterEnd;
    }


    @Override
    public void render(Mat frame, int currentFrame) {
        if (currentFrame < frameStart || (currentFrame > frameEnd && !keepAfterEnd)) {
            return;
        }
        try (Mat image = opencv_imgcodecs.imread(filePath.getAbsolutePath())) {
            if (image.empty()) {
                System.err.println("Nie udało się wczytać obrazu: " + filePath);
                return;
            }
            Rect roi = new Rect(position.getLeft(), position.getTop(), image.cols(), image.rows());
            Mat submat = frame.apply(roi);
            image.copyTo(submat);
        }
    }
}