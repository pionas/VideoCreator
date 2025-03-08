package pl.excellentapp.ekonkursy.scene.elements;

import pl.excellentapp.ekonkursy.core.ProjectProperties;

public class ElementProvider {

    public static VideoElement createSubscribeElement(int width, int height, int frameRate) {
        return new VideoElement(
                ProjectProperties.Videos.SUBSCRIBE,
                new ElementPosition(height - 200, width / 2),
                2,
                1,
                frameRate,
                true,
                false,
                new ElementSize(530, 300),
                false
        );
    }

    public static SceneElement createWomensDayElement(int width, int height, int frameRate) {
        return new VideoElement(
                ProjectProperties.Videos.WOMENS_DAY,
                new ElementPosition(height / 2, width / 2),
                3,
                0,
                frameRate,
                false,
                true,
                new ElementSize(width, height),
                false
        );
    }

    public static SceneElement createConfettiElement(int width, int height, int frameRate) {
        return new VideoElement(
                ProjectProperties.Videos.CONFETTI,
                new ElementPosition(height / 2, width / 2),
                6,
                0,
                frameRate,
                true,
                false,
                new ElementSize(width, height),
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
                false,
                true,
                new ElementSize(width, height),
                false
        );
    }

    public static SceneElement createFluidGradientElement(int width, int height, int frameRate, int displayDuration) {
        return new VideoElement(
                ProjectProperties.Videos.FLUID_GRADIENT,
                new ElementPosition(height / 2, width / 2),
                displayDuration,
                0,
                frameRate,
                false,
                true,
                new ElementSize(width, height),
                false
        );
    }
}
