package pl.excellentapp.ekonkursy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class VideoConfig {

    public static final String EKONKURSY_API_URL = "https://www.e-konkursy.info";
    public static final String OUTPUT_FILE = "output.mp4";
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public static final int FRAME_RATE = 30;

}
