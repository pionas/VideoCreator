package pl.excellentapp.ekonkursy.scene.effects;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

public class ScrollingEffect implements SceneEffect {

    @Override
    public Mat applyEffect(SceneConfig sceneConfig, SceneElement sceneElement, Mat frame, Mat image, int currentFrame) {
        int frameStart = sceneElement.getFrameStart();
        int frameEnd = sceneElement.getFrameEnd();
        int totalFrames = frameEnd - frameStart;
        if (currentFrame < frameStart || currentFrame > frameEnd) {
            return image;
        }

        int maxScrollOffset = image.rows() - sceneConfig.getHeight();
        if (maxScrollOffset <= 0) {
            return image;
        }
        double progress = (double) (currentFrame - frameStart) / totalFrames;
        int scrollOffset = (int) (progress * maxScrollOffset);
        Rect croppedArea = new Rect(0, scrollOffset, sceneConfig.getWidth(), sceneConfig.getHeight());
        return new Mat(image, croppedArea);
    }
}
