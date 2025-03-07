package pl.excellentapp.ekonkursy.scene.elements;

import org.bytedeco.opencv.opencv_core.Mat;

public interface SceneElement {

    ElementPosition getPosition();

    void render(Mat frame);
}
