package pl.excellentapp.ekonkursy.video;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.video.screens.Screen;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class VideoProjectConfig {

    private final String outputFile;
    private final int width;
    private final int height;
    private final int frameRate;
    private final List<Screen> screens;

    public boolean isValid() {
        return !screens.isEmpty() && width > 0 && height > 0 && frameRate > 0 && outputFile != null;
    }
}