package pl.excellentapp.ekonkursy.scene;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

import java.awt.Color;
import java.util.List;

@RequiredArgsConstructor
@Getter
@ToString
public class SceneConfig {

    private final int width;
    private final int height;
    private final Color backgroundColor;
    private final Color textColor;
    private final double durationInSeconds;
    private final SceneMargin margin;
    private final List<SceneElement> elements;

}
