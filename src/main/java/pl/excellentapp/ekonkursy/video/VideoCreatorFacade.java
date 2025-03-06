package pl.excellentapp.ekonkursy.video;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.VideoConfig;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.core.DirectoryCleaner;
import pl.excellentapp.ekonkursy.image.ImageProcessorService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class VideoCreatorFacade {

    private final ArticleFetcher articleFetcher;
    private final ArticleImageDownloader imageDownloader;
    private final ImageProcessorService imageProcessorService;
    private final VideoRecorder videoRecorder;
    private final DirectoryCleaner imageDirectoryCleaner;

    public void createVideo() {
        List<Article> articles = articleFetcher.fetchArticles();
        if (articles.isEmpty()) {
            System.out.println("Brak artykułów do przetworzenia.");
            return;
        }
        imageDownloader.downloadImages(articles);
        imageProcessorService.processImages(articles);

        Set<String> thankYouNames = getUsernameToThankYou(articles);
        videoRecorder.recordVideo(articles, VideoConfig.OUTPUT_FILE, VideoConfig.WIDTH, VideoConfig.HEIGHT, VideoConfig.FRAME_RATE, thankYouNames);

        imageDirectoryCleaner.clean();
    }

    private Set<String> getUsernameToThankYou(List<Article> articles) {
        return articles.stream()
                .map(Article::getUserName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
