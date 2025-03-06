package pl.excellentapp.ekonkursy.image.filters;

import org.bytedeco.opencv.opencv_core.Mat;

public interface ImageFilter {

    Mat apply(Mat image);
}
