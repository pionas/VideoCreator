package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.imgproc.Imgproc;

@Getter
public class TextElement extends SceneElement {

    private final String text;
    private final int fontSize;
    private final Scalar color;

    public TextElement(String text, ElementPosition position, int displayDuration, int delay, int fontSize, Scalar color, int fps, Size size) {
        super(position, displayDuration, delay, fps, size);
        this.text = text;
        this.fontSize = fontSize;
        this.color = color;
    }

    @Override
    public void render(Mat frame, int currentFrame) {
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
