package pl.excellentapp.ekonkursy.scene.effects;

import org.bytedeco.opencv.opencv_core.Mat;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

public class FadeEffect implements SceneEffect {

    private final int fadeInDuration;
    private final int fadeOutDuration;

    public FadeEffect(int fadeInDuration, int fadeOutDuration, int fps) {
        this.fadeInDuration = fadeInDuration * fps;
        this.fadeOutDuration = fadeOutDuration * fps;
    }

    @Override
    public Mat applyEffect(SceneConfig sceneConfig, SceneElement sceneElement, Mat frame, Mat image, int currentFrame) {
        int frameStart = sceneElement.getFrameStart();
        int frameEnd = sceneElement.getFrameEnd();

        if (currentFrame < frameStart || currentFrame > frameEnd) {
            return image;
        }

        double alpha = 1.0;

        if (currentFrame < frameStart + fadeInDuration) {
            alpha = (double) (currentFrame - frameStart) / fadeInDuration;
        } else if (currentFrame > frameEnd - fadeOutDuration) {
            alpha = (double) (frameEnd - currentFrame) / fadeOutDuration;
        }
        alpha = Math.max(0.0, Math.min(1.0, alpha));

        Mat fadedImage = new Mat();
        image.convertTo(fadedImage, -1, alpha, 0);
        return fadedImage;
    }
}
