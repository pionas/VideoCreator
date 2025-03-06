package pl.excellentapp.ekonkursy.video.screens;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.MovieConfig;
import pl.excellentapp.ekonkursy.article.ArticleImageService;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.video.ExecutionMode;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static pl.excellentapp.ekonkursy.MovieConfig.WELCOME_IMAGE_FILE;

@RequiredArgsConstructor
public class ScreenFactory {

    private final ThankYouScreenGenerator thankYouScreenGenerator;
    private final ArticleImageService imageService;

    public List<Screen> createScreens(List<Article> articles, int width, int height, int frameRate) {
        return List.of(
                createWelcomeScreen(width, height),
                createListOfArticleScreen(articles, width, height, frameRate),
                thankYouScreenGenerator.createThankYouScreen(articles, width, height, frameRate)
        );
    }

    private Screen createWelcomeScreen(int width, int height) {
        return ImageMovieScreen.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(MovieConfig.BACKGROUND_COLOR_WHITE)
                .images(List.of(
                        ImageConfig.builder()
                                .file(new File(WELCOME_IMAGE_FILE))
                                .frames(30)
                                .position(new Position(height / 2, width / 2))
                                .build()
                ))
                .maxFrames(30)
                .build();
    }

    private Screen createListOfArticleScreen(List<Article> articles, int width, int height, int frameRate) {
        AtomicInteger delay = new AtomicInteger();
        List<ImageConfig> imagesFromArticles = imageService.getImageConfigs(articles, delay, width, height);
        return ImageMovieScreen.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(MovieConfig.BACKGROUND_COLOR_WHITE)
                .images(imagesFromArticles)
                .videos(List.of(
                        VideoConfig.builder()
                                .file(new File(MovieConfig.EFFECT_FILE))
                                .loop(false)
                                .executionMode(ExecutionMode.NON_BLOCKING)
                                .position(new Position(height / 2, width / 2))
                                .order(Order.START)
                                .delayFrames(0)
                                .build()
                ))
                .maxFrames((imagesFromArticles.size() * delay.get() + frameRate))
                .build();
    }
}