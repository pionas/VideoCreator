package pl.excellentapp.ekonkursy;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;

import java.io.File;

import static pl.excellentapp.ekonkursy.VideoConfig.BACKGROUND_COLOR;

public class ImageProcessor {

    public File processImage(File imageFile, int maxWidth, int maxHeight) {
        if (imageFile == null || !imageFile.exists() || !isValidImage(imageFile)) {
            System.out.println("Nieprawidłowy obraz: " + (imageFile != null ? imageFile.getAbsolutePath() : "null"));
            return null;
        }

        Mat img = opencv_imgcodecs.imread(imageFile.getAbsolutePath());

        Size newSize = calculateAspectFitSize(img.size(), maxWidth, maxHeight);
        Mat resizedImg = resizeImage(img, newSize);

        Mat finalImage = addPadding(resizedImg, maxWidth, maxHeight);

        String outputPath = imageFile.getParent() + File.separator + "processed_" + imageFile.getName();
        opencv_imgcodecs.imwrite(outputPath, finalImage);

        return new File(outputPath);
    }

    private boolean isValidImage(File imageFile) {
        try (Mat img = opencv_imgcodecs.imread(imageFile.getAbsolutePath())) {
            return !img.empty();
        }
    }

    private Size calculateAspectFitSize(Size originalSize, int maxWidth, int maxHeight) {
        double aspectRatio = originalSize.width() / (double) originalSize.height();
        int targetWidth = maxWidth;
        int targetHeight = (int) (maxWidth / aspectRatio);

        if (targetHeight > maxHeight) {
            targetHeight = maxHeight;
            targetWidth = (int) (maxHeight * aspectRatio);
        }

        return new Size(targetWidth, targetHeight);
    }

    private Mat resizeImage(Mat img, Size newSize) {
        Mat resized = new Mat();
        opencv_imgproc.resize(img, resized, newSize, 0, 0, opencv_imgproc.INTER_AREA);
        return resized;
    }

    private Mat addPadding(Mat img, int finalWidth, int finalHeight) {
        Mat canvas = new Mat(finalHeight, finalWidth, img.type(), BACKGROUND_COLOR);
        int xOffset = (finalWidth - img.cols()) / 2;
        int yOffset = (finalHeight - img.rows()) / 2;

        Rect roi = new Rect(xOffset, yOffset, img.cols(), img.rows());
        Mat submat = canvas.apply(roi);
        img.copyTo(submat);

        return canvas;
    }
}