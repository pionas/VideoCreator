package pl.excellentapp.ekonkursy.scene.effects;

import org.bytedeco.opencv.opencv_core.Mat;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

public interface SceneEffect {

    Mat applyEffect(SceneConfig sceneConfig, SceneElement sceneElement, Mat frame, Mat image, int currentFrame);
}

