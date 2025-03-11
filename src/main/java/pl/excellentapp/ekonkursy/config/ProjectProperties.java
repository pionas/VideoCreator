package pl.excellentapp.ekonkursy.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProjectProperties {

    public static final String EKONKURSY_API_URL = "https://www.e-konkursy.info";

    public static final Path TEMPORARY_DIRECTORY = Path.of("./temp");
    public static final Path OUTPUT_FILE = Path.of("output.mp4");

    public static final class Videos {

        public static final Path SUBSCRIBE = Path.of("./movies/subscribe.mp4");
        public static final Path CONFETTI = Path.of("./movies/confetti.mp4");
        public static final Path WOMENS_DAY = Path.of("./movies/womens-day.mp4");
        public static final Path FLUID_GRADIENT = Path.of("./movies/fluid-gradient.mp4");
        public static final Path LAST_CHANCE = Path.of("./movies/last-chance.mp4");
        public static final Path EFFECT = Path.of("./movies/effect.mp4");
        public static final Path INTRO_NOW_OPEN = Path.of("./movies/intro-now-open.mp4");
        public static final Path BACKGROUND_STARS = Path.of("./movies/background-stars.mp4");
    }

    public static final class Images {

        public static final Path WELCOME = Path.of("./images/e-KonkursyInfo.jpg");
    }

    public static final class VideoSettings {

        public static final int WIDTH = 1080;
        public static final int HEIGHT = 1920;
        public static final int FRAME_RATE = 30;
    }

    public static final class Margins {

        public static final int TOP = 430;
        public static final int BOTTOM = 480;
        public static final int LEFT = 30;
        public static final int RIGHT = 30;
    }
}
