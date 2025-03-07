package pl.excellentapp.ekonkursy.filters;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;

public class BorderFilter implements MatFilter {
    private final int borderSize;
    private final Scalar borderColor;

    public BorderFilter(int borderSize, Scalar borderColor) {
        this.borderSize = borderSize;
        this.borderColor = borderColor;
    }

    @Override
    public Mat apply(Mat image) {
        Mat canvas = new Mat(image.rows() + 2 * borderSize, image.cols() + 2 * borderSize, image.type(), borderColor);

        Rect roi = new Rect(borderSize, borderSize, image.cols(), image.rows());

        Mat roiMat = canvas.apply(roi);
        image.copyTo(roiMat);

        return canvas;
    }
}