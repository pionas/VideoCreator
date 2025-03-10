package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.effects.SceneEffect;

import java.nio.file.Path;
import java.util.List;

@Getter
public class ImageElement extends SceneElement {

    private final Path filePath;
    private final boolean keepAfterEnd;

    public ImageElement(Path filePath, ElementPosition position, double displayDuration, double delay, int fps, boolean keepAfterEnd, ElementSize size, boolean considerMargins) {
        super(position, displayDuration, delay, fps, size, considerMargins);
        this.filePath = filePath;
        this.keepAfterEnd = keepAfterEnd;
    }

    public ImageElement(Path filePath, ElementPosition position, double displayDuration, double delay, int fps, boolean keepAfterEnd, ElementSize size, boolean considerMargins, List<SceneEffect> effects) {
        super(position, displayDuration, delay, fps, size, considerMargins, effects);
        this.filePath = filePath;
        this.keepAfterEnd = keepAfterEnd;
    }


    @Override
    public void render(SceneConfig config, Mat frame, int currentFrame) {
        if (currentFrame < frameStart || (currentFrame > frameEnd && !keepAfterEnd)) {
            return;
        }
        Mat image = opencv_imgcodecs.imread(filePath.toString());
        if (image.empty()) {
            System.err.println("Nie udało się wczytać obrazu: " + filePath);
            return;
        }
        addToVideoFrame(config, frame, image, currentFrame);
    }
}