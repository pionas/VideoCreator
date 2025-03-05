package pl.excellentapp.ekonkursy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bytedeco.opencv.opencv_core.Scalar;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class VideoConfig {

    public static final String EKONKURSY_API_URL = "https://www.e-konkursy.info";
    public static final String OUTPUT_FILE = "output.mp4";
    public static final String WELCOME_FILE = "./movies/welcome.mp4";
    public static final String EFFECT_FILE = "./movies/effect.mp4";
    public static final int WIDTH = 1080;
    public static final int HEIGHT = 1920;
    public static final int FRAME_RATE = 30;
    public static final Scalar BACKGROUND_COLOR_BLACK = new Scalar(0, 0, 0, 0);
    public static final Scalar BACKGROUND_COLOR_WHITE = new Scalar(255, 255, 255, 255);

}
