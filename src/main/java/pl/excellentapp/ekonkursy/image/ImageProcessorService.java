package pl.excellentapp.ekonkursy.image;

import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.Scalar;
import pl.excellentapp.ekonkursy.VideoConfig;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.image.filters.BorderFilter;
import pl.excellentapp.ekonkursy.image.filters.ImageFilter;
import pl.excellentapp.ekonkursy.image.filters.ResizeFilter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class ImageProcessorService {

    private final ImageProcessor imageProcessor;

    public void processImages(List<Article> articles) {
        List<ImageFilter> filters = Arrays.asList(
                new ResizeFilter(VideoConfig.WIDTH, VideoConfig.HEIGHT, VideoConfig.BACKGROUND_COLOR_BLACK),
                new BorderFilter(20, new Scalar(126, 126, 126, 126))
        );
        articles.forEach(article -> {
            File processedFile = imageProcessor.processImage(article.getImageFile(), filters);
            article.addFile(processedFile);
        });
    }
}
