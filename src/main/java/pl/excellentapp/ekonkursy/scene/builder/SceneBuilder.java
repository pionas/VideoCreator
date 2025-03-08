package pl.excellentapp.ekonkursy.scene.builder;

import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class SceneBuilder {

    private int width = 1920;
    private int height = 1080;
    private Color backgroundColor = Color.WHITE;
    private Color textColor = Color.BLACK;
    private int durationInSeconds = 5;
    private SceneMargin margin = SceneMargin.builder().build();
    private final List<SceneElement> elements = new ArrayList<>();

    public SceneBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public SceneBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public SceneBuilder setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public SceneBuilder setTextColor(Color textColor) {
        this.textColor = textColor;
        return this;
    }

    public SceneBuilder setDuration(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
        return this;
    }

    public SceneBuilder setSceneMargin(SceneMargin margin) {
        this.margin = margin;
        return this;
    }

    public SceneBuilder addElement(SceneElement element) {
        this.elements.add(element);
        return this;
    }

    public SceneConfig build() {
        return new SceneConfig(width, height, backgroundColor, textColor, durationInSeconds, margin, elements);
    }
}