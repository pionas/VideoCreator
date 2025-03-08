package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.imgproc.Imgproc;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;

@Getter
public class TextElement extends SceneElement {

    private final String text;
    private final int fontSize;
    private final Scalar color;

    public TextElement(String text, ElementPosition position, int displayDuration, int delay, int fontSize, Scalar color, int fps, Size size, boolean considerMargins) {
        super(position, displayDuration, delay, fps, size, considerMargins);
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
    }

    @Override
    public void render(SceneMargin margin, Mat frame, int currentFrame) {
        if (currentFrame < frameStart || currentFrame > frameEnd) {
            return;
        }
        opencv_imgproc.putText(
                frame,
                text,
                new Point(position.getLeft() / 2, position.getTop() / 2),
                Imgproc.FONT_HERSHEY_SIMPLEX,
                fontSize,
                color
        );
    }
}
