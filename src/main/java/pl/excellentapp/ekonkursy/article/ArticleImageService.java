package pl.excellentapp.ekonkursy.article;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.core.ProjectProperties;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.filters.ResizeFilter;
import pl.excellentapp.ekonkursy.scene.screens.ImageConfig;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class ArticleImageService {

    private final ArticleImageDownloader imageDownloader;
    private final ArticleImageProcessorService articleImageProcessorService;

    public List<ImageConfig> getImageConfigs(List<Article> articles, AtomicInteger delay, int width, int height) {
        imageDownloader.downloadImages(articles);
        ResizeFilter resizeFilter = new ResizeFilter(width - ProjectProperties.MARGIN_LEFT - ProjectProperties.MARGIN_RIGHT, height - ProjectProperties.MARGIN_TOP - ProjectProperties.MARGIN_BOTTOM);
        articleImageProcessorService.processImages(articles, List.of(resizeFilter));
        return articles.stream()
                .map(article -> createImageConfig(delay, article, width, height))
                .toList();
    }

    private ImageConfig createImageConfig(AtomicInteger delay, Article article, int width, int height) {
        return ImageConfig.builder()
                .file(article.getImageFile())
                .frames(ProjectProperties.FRAME_RATE)
                .delayFrames(10 + ProjectProperties.FRAME_RATE * (delay.getAndIncrement()))
                .elementPosition(new ElementPosition(height / 2, width / 2))
                .build();
    }
}
