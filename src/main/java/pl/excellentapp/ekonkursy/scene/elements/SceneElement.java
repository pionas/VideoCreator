package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.opencv.opencv_core.Mat;

@Getter
public abstract class SceneElement {

    protected final ElementPosition position;
    protected final int frameStart;
    protected final int frameEnd;

    public SceneElement(ElementPosition position, int displayDuration, int delay, int fps) {
        this.position = position;
        this.frameStart = delay * fps;
        this.frameEnd = this.frameStart + (displayDuration * fps);
    }

    public abstract void render(Mat frame, int currentFrame);
}
