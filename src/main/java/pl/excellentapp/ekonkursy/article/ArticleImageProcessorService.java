package pl.excellentapp.ekonkursy.article;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.image.ImageFilterProcessor;
import pl.excellentapp.ekonkursy.filters.MatFilter;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public class ArticleImageProcessorService {

    private final ImageFilterProcessor imageFilterProcessor;

    public void processImages(List<Article> articles, List<MatFilter> filters) {
        articles.forEach(article -> {
            File processedFile = imageFilterProcessor.processImage(article.getImageFile(), filters);
            article.addFile(processedFile);
        });
    }
}
