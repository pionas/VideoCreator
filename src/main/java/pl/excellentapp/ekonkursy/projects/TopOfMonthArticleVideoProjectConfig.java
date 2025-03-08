package pl.excellentapp.ekonkursy.projects;

import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.IVideoProjectConfig;
import pl.excellentapp.ekonkursy.VideoProjectConfig;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.core.ProjectProperties;
import pl.excellentapp.ekonkursy.image.ThankYouImageGenerator;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.builder.SceneBuilder;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ImageElement;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;
import pl.excellentapp.ekonkursy.scene.elements.VideoElement;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static pl.excellentapp.ekonkursy.core.ProjectProperties.MARGIN_BOTTOM;
import static pl.excellentapp.ekonkursy.core.ProjectProperties.MARGIN_LEFT;
import static pl.excellentapp.ekonkursy.core.ProjectProperties.MARGIN_RIGHT;
import static pl.excellentapp.ekonkursy.core.ProjectProperties.MARGIN_TOP;
import static pl.excellentapp.ekonkursy.core.ProjectProperties.WELCOME_IMAGE_FILE;

public class TopOfMonthArticleVideoProjectConfig implements IVideoProjectConfig {

    private final ArticleImageDownloader imageDownloader;
    private final List<Article> articles;
    private final int width;
    private final int height;
    private final int frameRate;

    public TopOfMonthArticleVideoProjectConfig(ArticleImageDownloader imageDownloader, ArticleFetcher articleFetcher) {
        this.imageDownloader = imageDownloader;
        this.articles = articleFetcher.top("month");
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

    private SceneConfig createWelcomeScreen() {
        int durationInSeconds = 2;
        return new SceneBuilder()
                .setBackgroundColor(Color.WHITE)
                .setTextColor(Color.BLACK)
                .setWidth(width)
                .setHeight(height)
                .setDuration(durationInSeconds)
                .addElement(getImageElement(new File(WELCOME_IMAGE_FILE), durationInSeconds, 0, frameRate, true))
//                .addElement(new TextElement("Hot miesiąca", new ElementPosition(height - 300, width / 2), durationInSeconds, 0, 20, new Scalar(128, 128, 128, 128), frameRate, new Size(100, 100)))
                .build();
    }

    private SceneConfig createListOfArticleScreen() {
        AtomicInteger delay = new AtomicInteger();
        int displayDuration = articles.size();
        SceneBuilder sceneBuilder = new SceneBuilder()
                .setWidth(width)
                .setHeight(height)
                .setSceneMargin(getSceneMargin())
                .addElement(new VideoElement(
                        new File(ProjectProperties.EFFECT_FILE),
                        new ElementPosition(height / 2, width / 2),
                        displayDuration,
                        0,
                        frameRate,
                        false,
                        true,
                        new Size(width, height)
                ))
                .setDuration(displayDuration);

        articles.forEach(article -> {
            imageDownloader.downloadImages(articles);
            sceneBuilder.addElement(getImageElement(article.getImageFile(), 1, delay.getAndIncrement(), frameRate, false));
        });
        return sceneBuilder.build();
    }

    public SceneConfig createThankYouScreen() {
        Set<String> thankYouNames = getUsernameToThankYou(articles);
        File file = new ThankYouImageGenerator(thankYouNames, width, height).generateThankYouImage();

        return new SceneBuilder()
                .setBackgroundColor(Color.WHITE)
                .setTextColor(Color.BLACK)
                .setWidth(width)
                .setHeight(height)
                .addElement(getImageElement(file, 2, 0, frameRate, true))
                .addElement(getVideoElement())
                .setDuration(2)
                .build();
    }

    private Set<String> getUsernameToThankYou(List<Article> articles) {
        return articles.stream()
                .map(Article::getUserName)
                .collect(Collectors.toUnmodifiableSet());
    }

    private SceneMargin getSceneMargin() {
        return SceneMargin.builder()
                .top(ProjectProperties.MARGIN_TOP)
                .right(ProjectProperties.MARGIN_RIGHT)
                .bottom(ProjectProperties.MARGIN_BOTTOM)
                .left(ProjectProperties.MARGIN_LEFT)
                .build();
    }

    private ImageElement getImageElement(File file, int durationInSeconds, int delay, int fps, boolean keepAfterEnd) {
        return new ImageElement(
                file,
                new ElementPosition(height / 2, width / 2),
                durationInSeconds,
                delay,
                fps,
                keepAfterEnd,
                new Size(width - MARGIN_LEFT - MARGIN_RIGHT, height - MARGIN_TOP - MARGIN_BOTTOM)
        );
    }

    private SceneElement getVideoElement() {
        return new VideoElement(
                new File(ProjectProperties.SUBSCRIBE_FILE),
                new ElementPosition(height - 300, width / 2),
                2,
                0,
                frameRate,
                true,
                false,
                new Size(width, height)
        );
    }
}