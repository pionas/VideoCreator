package pl.excellentapp.ekonkursy.video;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.MovieConfig;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.core.DirectoryCleaner;
import pl.excellentapp.ekonkursy.image.ImageProcessorService;
import pl.excellentapp.ekonkursy.image.filters.ResizeFilter;
import pl.excellentapp.ekonkursy.video.screens.ImageConfig;
import pl.excellentapp.ekonkursy.video.screens.ImageMovieScreen;
import pl.excellentapp.ekonkursy.video.screens.Position;
import pl.excellentapp.ekonkursy.video.screens.Screen;
import pl.excellentapp.ekonkursy.video.screens.ThankYouScreen;
import pl.excellentapp.ekonkursy.video.screens.VideoConfig;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static pl.excellentapp.ekonkursy.MovieConfig.WELCOME_IMAGE_FILE;

@RequiredArgsConstructor
public class VideoCreatorFacade {

    private final ArticleFetcher articleFetcher;
    private final ArticleImageDownloader imageDownloader;
    private final ImageProcessorService imageProcessorService;
    private final VideoRecorder videoRecorder;
    private final DirectoryCleaner imageDirectoryCleaner;

    public void createVideo(String outputFile, int width, int height, int frameRate) {
        List<Article> articles = articleFetcher.fetchArticles();
        if (articles.isEmpty()) {
            System.out.println("Brak artykułów do przetworzenia.");
            return;
        }

        List<Screen> screens = List.of(
                getWelcomeScreen(width, height),
                getListOfArticleScreen(articles, width, height, frameRate),
                getThankYouScreen(articles, width, height)
        );
        videoRecorder.recordVideo(outputFile, screens, width, height, frameRate);

        imageDirectoryCleaner.clean();
    }

    private Screen getWelcomeScreen(int width, int height) {
        List<ImageConfig> images = List.of(
                new ImageConfig(new File(WELCOME_IMAGE_FILE), 30, new Position(height / 2, width / 2))
        );
        return ImageMovieScreen.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(MovieConfig.BACKGROUND_COLOR_WHITE)
                .images(images)
                .build();
    }

    private Screen getListOfArticleScreen(List<Article> articles, int width, int height, int frameRate) {
        List<VideoConfig> videos = List.of(
                VideoConfig.nonBlocking(new File(MovieConfig.EFFECT_FILE), new Position(height / 2, width / 2))
        );

        imageDownloader.downloadImages(articles);
        ResizeFilter resizeFilter = new ResizeFilter((MovieConfig.WIDTH - MovieConfig.MARGIN_LEFT - MovieConfig.MARGIN_RIGHT), (MovieConfig.HEIGHT - MovieConfig.MARGIN_TOP - MovieConfig.MARGIN_BOTTOM));
        imageProcessorService.processImages(articles, List.of(resizeFilter));
        AtomicInteger delay = new AtomicInteger();
        List<ImageConfig> imagesFromArticles = articles.stream()
                .map(article -> new ImageConfig(article.getImageFile(), frameRate, (frameRate * (delay.getAndIncrement())), new Position(height / 2, width / 2)))
                .toList();
        return ImageMovieScreen.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(MovieConfig.BACKGROUND_COLOR_WHITE)
                .images(imagesFromArticles)
                .videos(videos)
                .build();
    }

    private Screen getThankYouScreen(List<Article> articles, int width, int height) {
        Set<String> thankYouNames = getUsernameToThankYou(articles);
        return new ThankYouScreen(thankYouNames, 30, width, height);
    }

    private Set<String> getUsernameToThankYou(List<Article> articles) {
        return articles.stream()
                .map(Article::getUserName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
