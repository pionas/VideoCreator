package pl.excellentapp.ekonkursy.scene.elements;

import pl.excellentapp.ekonkursy.config.ProjectProperties;

public class ElementProvider {

    public static VideoElement createSubscribeElement(int width, int height, int frameRate) {
        return new VideoElement(
                ProjectProperties.Videos.SUBSCRIBE,
                new ElementPosition(height - 200, width / 2),
                3,
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

    public static SceneElement createLastChanceElement(int width, int height, int frameRate, int displayDuration) {
        return new VideoElement(
                ProjectProperties.Videos.LAST_CHANCE,
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

    public static SceneElement createLastChanceElement2(int width, int height) {
        return new VideoElement(
                ProjectProperties.Videos.LAST_CHANCE_2,
                new ElementPosition(height / 2, width / 2),
                3,
                0,
                30,
                false,
                true,
                new ElementSize(width, height),
                false
        );
    }

    public static SceneElement createIntroLastAddedElement(int width, int height, int frameRate) {
        return new VideoElement(
                ProjectProperties.Videos.INTRO_NOW_OPEN,
                new ElementPosition(height / 2, width / 2),
                8,
                0,
                frameRate,
                false,
                true,
                new ElementSize(width, height),
                false
        );
    }

    public static SceneElement createBackgroundStarsElement(int width, int height, int frameRate) {
        return new VideoElement(
                ProjectProperties.Videos.BACKGROUND_STARS,
                new ElementPosition(height / 2, width / 2),
                35,
                0,
                frameRate,
                true,
                false,
                new ElementSize(width, height),
                false
        );
    }
}
