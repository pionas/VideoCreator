package pl.excellentapp.ekonkursy.video.filters;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

public class ResizeFilter implements MatFilter {

    private final int maxWidth;
    private final int maxHeight;
    private final Scalar backgroundColor;

    public ResizeFilter(int maxWidth, int maxHeight) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.backgroundColor = null;
    }

    public ResizeFilter(int maxWidth, int maxHeight, Scalar backgroundColor) {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public Mat apply(Mat image) {
        Size newSize = calculateAspectFitSize(image.size(), maxWidth, maxHeight);
        Mat resized = new Mat();
        opencv_imgproc.resize(image, resized, newSize, 0, 0, opencv_imgproc.INTER_AREA);
        if (backgroundColor == null) {
            return resized;
        }
        return addPadding(resized, maxWidth, maxHeight);
    }

    private Size calculateAspectFitSize(Size originalSize, int maxWidth, int maxHeight) {
        if (originalSize.width() <= 0 || originalSize.height() <= 0) {
            throw new IllegalArgumentException("Rozmiar obrazu nie może mieć zerowej szerokości ani wysokości");
        }
        double aspectRatio = originalSize.width() / (double) originalSize.height();
        int targetWidth = maxWidth;
        int targetHeight = (int) (maxWidth / aspectRatio);

        if (targetHeight > maxHeight) {
            targetHeight = maxHeight;
            targetWidth = (int) (maxHeight * aspectRatio);
        }

        return new Size(targetWidth, targetHeight);
    }

    private Mat addPadding(Mat img, int finalWidth, int finalHeight) {
        Mat canvas = new Mat(finalHeight, finalWidth, img.type(), backgroundColor);
        int xOffset = (finalWidth - img.cols()) / 2;
        int yOffset = (finalHeight - img.rows()) / 2;

        Rect roi = new Rect(xOffset, yOffset, img.cols(), img.rows());
        Mat submat = canvas.apply(roi);
        img.copyTo(submat);

        return canvas;
    }

}
