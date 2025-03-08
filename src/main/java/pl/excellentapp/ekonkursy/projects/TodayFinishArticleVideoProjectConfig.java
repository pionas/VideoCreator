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
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TodayFinishArticleVideoProjectConfig implements IVideoProjectConfig {

    private final ArticleImageDownloader imageDownloader;
    private final List<Article> articles;
    private final int width;
    private final int height;
    private final int frameRate;

    public TodayFinishArticleVideoProjectConfig(ArticleImageDownloader imageDownloader, ArticleFetcher articleFetcher) {
        this.imageDownloader = imageDownloader;
        this.articles = articleFetcher.end();
        this.width = ProjectProperties.VideoSettings.WIDTH;
        this.height = ProjectProperties.VideoSettings.HEIGHT;
        this.frameRate = ProjectProperties.VideoSettings.FRAME_RATE;
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
                .addElement(getImageElement(ProjectProperties.Images.WELCOME, durationInSeconds, 0, frameRate, true))
//                .addElement(new TextElement("Ostatnia szansa", new ElementPosition(height - 300, width / 2), durationInSeconds, 0, 20, new Scalar(128, 128, 128, 128), frameRate, new Size(100, 100)))
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
                        ProjectProperties.Videos.EFFECT,
                        new ElementPosition(height / 2, width / 2),
                        displayDuration,
                        0,
                        frameRate,
                        false,
                        true,
                        new Size(width, height),
                        false
                ))
                .setDuration(displayDuration);

        articles.forEach(article -> {
            imageDownloader.downloadImages(articles);
            sceneBuilder.addElement(getImageElement(article.getImageFile().toPath(), 1, delay.getAndIncrement(), frameRate, false));
        });
        return sceneBuilder.build();
    }

    public SceneConfig createThankYouScreen() {
        Set<String> thankYouNames = getUsernameToThankYou(articles);
        Path filePath = new ThankYouImageGenerator(thankYouNames, width, height).generateThankYouImage();

        return new SceneBuilder()
                .setBackgroundColor(Color.WHITE)
                .setTextColor(Color.BLACK)
                .setWidth(width)
                .setHeight(height)
                .addElement(getImageElement(filePath, 2, 0, frameRate, true))
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
                .top(ProjectProperties.Margins.TOP)
                .right(ProjectProperties.Margins.RIGHT)
                .bottom(ProjectProperties.Margins.BOTTOM)
                .left(ProjectProperties.Margins.LEFT)
                .build();
    }

    private ImageElement getImageElement(Path filePath, int durationInSeconds, int delay, int fps, boolean keepAfterEnd) {
        return new ImageElement(
                filePath,
                new ElementPosition(height / 2, width / 2),
                durationInSeconds,
                delay,
                fps,
                keepAfterEnd,
                new Size(width - ProjectProperties.Margins.LEFT - ProjectProperties.Margins.RIGHT, height - ProjectProperties.Margins.TOP - ProjectProperties.Margins.BOTTOM),
                true
        );
    }

    private SceneElement getVideoElement() {
        return new VideoElement(
                ProjectProperties.Videos.SUBSCRIBE,
                new ElementPosition(height - 300, width / 2),
                2,
                0,
                frameRate,
                true,
                false,
                new Size(width, height),
                false
        );
    }
}