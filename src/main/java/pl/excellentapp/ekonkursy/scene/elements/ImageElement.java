package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;

import java.nio.file.Path;

@Getter
public class ImageElement extends SceneElement {

    private final Path filePath;
    private final boolean keepAfterEnd;

    public ImageElement(Path filePath, ElementPosition position, int displayDuration, int delay, int fps, boolean keepAfterEnd, Size size, boolean considerMargins) {
        super(position, displayDuration, delay, fps, size, considerMargins);
        this.filePath = filePath;
        this.keepAfterEnd = keepAfterEnd;
    }


    @Override
    public void render(SceneMargin margin, Mat frame, int currentFrame) {
        if (currentFrame < frameStart || (currentFrame > frameEnd && !keepAfterEnd)) {
            return;
        }
        Mat image = opencv_imgcodecs.imread(filePath.toString());
        if (image.empty()) {
            System.err.println("Nie udało się wczytać obrazu: " + filePath);
            return;
        }
        addToVideoFrame(margin, frame, image);
    }
}