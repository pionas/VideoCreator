package pl.excellentapp.ekonkursy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.scene.Scene;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class VideoProjectConfig {

    private final String outputFile;
    private final int width;
    private final int height;
    private final int frameRate;
    private final List<Scene> scenes;

    public boolean isValid() {
        return !scenes.isEmpty() && width > 0 && height > 0 && frameRate > 0 && outputFile != null;
    }
}