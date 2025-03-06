package pl.excellentapp.ekonkursy.image;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
import pl.excellentapp.ekonkursy.video.filters.MatFilter;

import java.io.File;
import java.util.List;

public class ImageProcessor {

    public File processImage(File imageFile, List<MatFilter> filters) {
        if (imageFile == null || !imageFile.exists()) {
            System.out.println("Nieprawidłowy obraz: " + (imageFile != null ? imageFile.getAbsolutePath() : "null"));
            return null;
        }
        Mat img = opencv_imgcodecs.imread(imageFile.getAbsolutePath());
        for (MatFilter filter : filters) {
            img = filter.apply(img);
        }
        String outputPath = imageFile.getParent() + File.separator + "processed_" + imageFile.getName();
        opencv_imgcodecs.imwrite(outputPath, img);

        return new File(outputPath);
    }
}