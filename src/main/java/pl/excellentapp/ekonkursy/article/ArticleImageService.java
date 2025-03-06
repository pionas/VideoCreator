package pl.excellentapp.ekonkursy.article;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.MovieConfig;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.video.filters.ResizeFilter;
import pl.excellentapp.ekonkursy.video.screens.ImageConfig;
import pl.excellentapp.ekonkursy.video.screens.Position;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class ArticleImageService {

    private final ArticleImageDownloader imageDownloader;
    private final ArticleImageProcessorService articleImageProcessorService;

    public List<ImageConfig> getImageConfigs(List<Article> articles, AtomicInteger delay, int width, int height) {
        imageDownloader.downloadImages(articles);
        ResizeFilter resizeFilter = new ResizeFilter(width - MovieConfig.MARGIN_LEFT - MovieConfig.MARGIN_RIGHT, height - MovieConfig.MARGIN_TOP - MovieConfig.MARGIN_BOTTOM);
        articleImageProcessorService.processImages(articles, List.of(resizeFilter));
        return articles.stream()
                .map(article -> createImageConfig(delay, article, width, height))
                .toList();
    }

    private ImageConfig createImageConfig(AtomicInteger delay, Article article, int width, int height) {
        return ImageConfig.builder()
                .file(article.getImageFile())
                .frames(MovieConfig.FRAME_RATE)
                .delayFrames(10 + MovieConfig.FRAME_RATE * (delay.getAndIncrement()))
                .position(new Position(height / 2, width / 2))
                .build();
    }
}
