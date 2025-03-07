package pl.excellentapp.ekonkursy.projects;

import pl.excellentapp.ekonkursy.ExecutionMode;
import pl.excellentapp.ekonkursy.IVideoProjectConfig;
import pl.excellentapp.ekonkursy.VideoProjectConfig;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageService;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.core.ProjectProperties;
import pl.excellentapp.ekonkursy.image.ThankYouImageGenerator;
import pl.excellentapp.ekonkursy.scene.Scene;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ElementSize;
import pl.excellentapp.ekonkursy.scene.screens.ImageConfig;
import pl.excellentapp.ekonkursy.scene.screens.ImageMovieScene;
import pl.excellentapp.ekonkursy.scene.screens.Order;
import pl.excellentapp.ekonkursy.scene.screens.VideoConfig;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static pl.excellentapp.ekonkursy.core.ProjectProperties.WELCOME_IMAGE_FILE;

public class TodayFinishArticleVideoProjectConfig implements IVideoProjectConfig {

    private final ArticleImageService imageService;
    private final List<Article> articles;
    private final int width;
    private final int height;
    private final int frameRate;

    public TodayFinishArticleVideoProjectConfig(ArticleImageService imageService, ArticleFetcher articleFetcher) {
        this.imageService = imageService;
        this.articles = articleFetcher.end();
        this.width = ProjectProperties.WIDTH;
        this.height = ProjectProperties.HEIGHT;
        this.frameRate = ProjectProperties.FRAME_RATE;
    }

    public VideoProjectConfig toVideoProjectConfig() {
        return new VideoProjectConfig(
                ProjectProperties.OUTPUT_FILE,
                width,
                height,
                frameRate,
                List.of(
                        createWelcomeScreen(),
                        createListOfArticleScreen(),
                        createThankYouScreen()
                )
        );
    }

    private Scene createWelcomeScreen() {
        return ImageMovieScene.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(ProjectProperties.BACKGROUND_COLOR_WHITE)
                .images(List.of(
                        ImageConfig.builder()
                                .file(new File(WELCOME_IMAGE_FILE))
                                .frames(30)
                                .elementPosition(new ElementPosition(height / 2, width / 2))
                                .build()
                ))
                .maxFrames(30)
                .build();
    }

    private Scene createListOfArticleScreen() {
        AtomicInteger delay = new AtomicInteger();
        List<ImageConfig> imagesFromArticles = imageService.getImageConfigs(articles, delay, width, height);
        return ImageMovieScene.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(ProjectProperties.BACKGROUND_COLOR_WHITE)
                .images(imagesFromArticles)
                .videos(List.of(
                        VideoConfig.builder()
                                .file(new File(ProjectProperties.EFFECT_FILE))
                                .loop(false)
                                .executionMode(ExecutionMode.NON_BLOCKING)
                                .elementPosition(new ElementPosition(height / 2, width / 2))
                                .order(Order.START)
                                .delayFrames(0)
                                .build()
                ))
                .maxFrames((imagesFromArticles.size() * delay.get() + frameRate))
                .build();
    }

    public Scene createThankYouScreen() {
        Set<String> thankYouNames = getUsernameToThankYou(articles);
        File file = new ThankYouImageGenerator(thankYouNames, width, height).generateThankYouImage();

        return ImageMovieScene.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(ProjectProperties.BACKGROUND_COLOR_WHITE)
                .images(List.of(ImageConfig.builder()
                        .file(file)
                        .frames(frameRate * 3)
                        .elementPosition(new ElementPosition(height / 2, width / 2))
                        .build()))
                .videos(List.of(
                        VideoConfig.builder()
                                .file(new File(ProjectProperties.SUBSCRIBE_FILE))
                                .loop(true)
                                .executionMode(ExecutionMode.NON_BLOCKING)
                                .elementPosition(new ElementPosition(height - 300, width / 2))
                                .size(ElementSize.builder().maxWidth(200).maxHeight(200).build())
                                .order(Order.END)
                                .delayFrames(0)
                                .build()
                ))
                .maxFrames(frameRate * 3)
                .build();
    }

    private Set<String> getUsernameToThankYou(List<Article> articles) {
        return articles.stream()
                .map(Article::getUserName)
                .collect(Collectors.toUnmodifiableSet());
    }
}