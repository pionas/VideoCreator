package pl.excellentapp.ekonkursy.scene.builder;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ImageElement;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class FileSceneBuilder implements SceneBuilderStrategy {

    private final String directoryPath;

    @Override
    public SceneConfig buildScene() {
        File dir = new File(directoryPath);
        List<SceneElement> elements = new ArrayList<>();

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".png") || name.endsWith(".jpg"));
            if (files != null) {
                int yOffset = 50;
                for (File file : files) {
                    elements.add(new ImageElement(file.getAbsolutePath(), new ElementPosition(yOffset, 100)));
                    yOffset += 150;
                }
            }
        }

        return new SceneConfig(1920, 1080, Color.BLACK, Color.WHITE, 5, elements);
    }
}