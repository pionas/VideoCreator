package pl.excellentapp.ekonkursy.image;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.image.filters.ImageFilter;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public class ImageProcessorService {

    private final ImageProcessor imageProcessor;

    public void processImages(List<Article> articles, List<ImageFilter> filters) {
        articles.forEach(article -> {
            File processedFile = imageProcessor.processImage(article.getImageFile(), filters);
            article.addFile(processedFile);
        });
    }
}
