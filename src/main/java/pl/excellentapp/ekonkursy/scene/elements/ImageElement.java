package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

@RequiredArgsConstructor
@Getter
public class ImageElement implements SceneElement {

    private final String filePath;
    private final ElementPosition position;

    @Override
    public void render(Mat frame) {
        try (Mat image = opencv_imgcodecs.imread(filePath)) {
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