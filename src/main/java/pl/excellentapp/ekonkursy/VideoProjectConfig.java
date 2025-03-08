package pl.excellentapp.ekonkursy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.scene.SceneConfig;

import java.nio.file.Path;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class VideoProjectConfig {

    private final Path outputFile;
    private final int width;
    private final int height;
    private final int frameRate;
    private final List<SceneConfig> sceneConfigs;

}