package pl.excellentapp.ekonkursy.filters;

import org.bytedeco.opencv.opencv_core.Mat;

public interface MatFilter {

    Mat apply(Mat image);
}
