package pl.excellentapp.ekonkursy.video;

import pl.excellentapp.ekonkursy.MovieConfig;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.article.ArticleImageProcessorService;
import pl.excellentapp.ekonkursy.image.ThankYouImageGenerator;
import pl.excellentapp.ekonkursy.video.filters.ResizeFilter;
import pl.excellentapp.ekonkursy.video.screens.ElementSize;
import pl.excellentapp.ekonkursy.video.screens.ImageConfig;
import pl.excellentapp.ekonkursy.video.screens.ImageMovieScreen;
import pl.excellentapp.ekonkursy.video.screens.Order;
import pl.excellentapp.ekonkursy.video.screens.Position;
import pl.excellentapp.ekonkursy.video.screens.Screen;
import pl.excellentapp.ekonkursy.video.screens.VideoConfig;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static pl.excellentapp.ekonkursy.MovieConfig.WELCOME_IMAGE_FILE;

public class ArticleVideoProjectConfig {

    private final ArticleImageDownloader imageDownloader;
    private final ArticleImageProcessorService articleImageProcessorService;
    private final List<Article> articles;
    private final int width;
    private final int height;
    private final int frameRate;

    public ArticleVideoProjectConfig(ArticleFetcher articleFetcher, ArticleImageDownloader imageDownloader, ArticleImageProcessorService articleImageProcessorService) {
        this.imageDownloader = imageDownloader;
        this.articleImageProcessorService = articleImageProcessorService;
        this.articles = articleFetcher.fetchArticles();
        this.width = MovieConfig.WIDTH;
        this.height = MovieConfig.HEIGHT;
        this.frameRate = MovieConfig.FRAME_RATE;
    }

    public VideoProjectConfig toVideoProjectConfig() {
        return new VideoProjectConfig(
                MovieConfig.OUTPUT_FILE,
                width,
                height,
                frameRate,
                getScreens()
        );
    }

    private List<Screen> getScreens() {
        if (articles == null || articles.isEmpty()) {
            return Collections.emptyList();
        }
        return List.of(
                getWelcomeScreen(),
                getListOfArticleScreen(),
                getThankYouScreen()
        );
    }

    private Screen getWelcomeScreen() {
        int frames = 30;
        List<ImageConfig> images = List.of(
                ImageConfig.builder()
                        .file(new File(WELCOME_IMAGE_FILE))
                        .frames(frames)
                        .position(new Position(height / 2, width / 2))
                        .build()
        );
        return ImageMovieScreen.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(MovieConfig.BACKGROUND_COLOR_WHITE)
                .images(images)
                .maxFrames(frames)
                .build();
    }

    private Screen getListOfArticleScreen() {
        List<VideoConfig> videos = List.of(
                VideoConfig.builder()
                        .file(new File(MovieConfig.EFFECT_FILE))
                        .loop(false)
                        .executionMode(ExecutionMode.NON_BLOCKING)
                        .position(new Position(height / 2, width / 2))
                        .order(Order.START)
                        .delayFrames(0)
                        .build()
        );

        AtomicInteger delay = new AtomicInteger();
        List<ImageConfig> imagesFromArticles = getImageConfigs(delay);
        return ImageMovieScreen.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(MovieConfig.BACKGROUND_COLOR_WHITE)
                .images(imagesFromArticles)
                .videos(videos)
                .maxFrames((imagesFromArticles.size() * delay.get() + frameRate))
                .build();
    }

    private List<ImageConfig> getImageConfigs(AtomicInteger delay) {
        imageDownloader.downloadImages(articles);
        ResizeFilter resizeFilter = new ResizeFilter((MovieConfig.WIDTH - MovieConfig.MARGIN_LEFT - MovieConfig.MARGIN_RIGHT), (MovieConfig.HEIGHT - MovieConfig.MARGIN_TOP - MovieConfig.MARGIN_BOTTOM));
        articleImageProcessorService.processImages(articles, List.of(resizeFilter));
        return articles.stream()
                .map(article -> getImageConfig(delay, article))
                .toList();
    }

    private ImageConfig getImageConfig(AtomicInteger delay, Article article) {
        return ImageConfig.builder()
                .file(article.getImageFile())
                .frames(frameRate)
                .delayFrames(10 + frameRate * (delay.getAndIncrement()))
                .position(new Position(height / 2, width / 2))
                .build();
    }

    private Screen getThankYouScreen() {
        Set<String> thankYouNames = getUsernameToThankYou();
        File file = new ThankYouImageGenerator(thankYouNames, width, height).generateThankYouImage();
        ImageConfig image = ImageConfig.builder()
                .file(file)
                .frames(frameRate * 3)
                .position(new Position(height / 2, width / 2))
                .build();

        List<VideoConfig> videos = List.of(
                VideoConfig.builder()
                        .file(new File(MovieConfig.SUBSCRIBE_FILE))
                        .loop(true)
                        .executionMode(ExecutionMode.NON_BLOCKING)
                        .position(new Position(height - 300, width / 2))
                        .size(ElementSize.builder().maxWidth(200).maxHeight(200).build())
                        .order(Order.END)
                        .delayFrames(0)
                        .build()
        );

        return ImageMovieScreen.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(MovieConfig.BACKGROUND_COLOR_WHITE)
                .images(List.of(image))
                .videos(videos)
                .maxFrames(frameRate * 3)
                .build();
    }

    private Set<String> getUsernameToThankYou() {
        return articles.stream()
                .map(Article::getUserName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
