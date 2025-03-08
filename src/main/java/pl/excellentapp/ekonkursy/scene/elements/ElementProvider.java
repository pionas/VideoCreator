package pl.excellentapp.ekonkursy.scene.elements;

import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.core.ProjectProperties;

public class ElementProvider {

    public static VideoElement createSubscribeElement(int width, int height, int frameRate) {
        return new VideoElement(
                ProjectProperties.Videos.SUBSCRIBE,
                new ElementPosition(height - 300, width / 2),
                2,
                0,
                frameRate,
                true,
                false,
                new Size(1000, 300),
                false
        );
    }

    public static SceneElement createEffectElement(int width, int height, int frameRate, int displayDuration) {
        return new VideoElement(
                ProjectProperties.Videos.EFFECT,
                new ElementPosition(height / 2, width / 2),
                displayDuration,
                0,
                frameRate,
                true,
                false,
                new Size(width, height),
                false
        );
    }
}
