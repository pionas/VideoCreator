package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.opencv.imgproc.Imgproc;

@RequiredArgsConstructor
@Getter
public class TextElement implements SceneElement {

    private final String text;
    private final ElementPosition position;
    private final int fontSize;
    private final Scalar color;

    public TextElement(String text, ElementPosition position) {
        this.text = text;
        this.position = position;
        this.fontSize = 1;
        this.color = new Scalar(255, 255, 255, 0);
    }

    @Override
    public void render(Mat frame) {
        opencv_imgproc.putText(
                frame,
                text,
                new Point(position.getLeft(), position.getTop()),
                Imgproc.FONT_HERSHEY_SIMPLEX,
                fontSize,
                color
        );
    }
}
