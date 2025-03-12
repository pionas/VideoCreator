package pl.excellentapp.ekonkursy.projects;

import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.article.service.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.config.IVideoProjectConfig;
import pl.excellentapp.ekonkursy.config.ProjectProperties;
import pl.excellentapp.ekonkursy.config.VideoProjectConfig;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.image.ThankYouImageGenerator;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.builder.SceneBuilder;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ElementProvider;
import pl.excellentapp.ekonkursy.scene.elements.ElementSize;
import pl.excellentapp.ekonkursy.scene.elements.ImageElement;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

import java.awt.Color;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractArticleVideoProjectConfig implements IVideoProjectConfig {

    protected final ArticleImageDownloader imageDownloader;
    protected final ImageProcessor imageProcessor;
    protected final List<Article> articles;
    protected final int width;
    protected final int height;
    protected final int frameRate;

    protected AbstractArticleVideoProjectConfig(ArticleImageDownloader imageDownloader, ImageProcessor imageProcessor, List<Article> articles, int width, int height, int frameRate) {
        this.imageDownloader = imageDownloader;
        this.imageProcessor = imageProcessor;
        this.articles = articles;
        this.width = width;
        this.height = height;
        this.frameRate = frameRate;
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

    protected abstract double getIntroDuration();

    protected abstract List<SceneElement> getIntroElements();

    protected abstract SceneConfig createListOfArticleScreen();

    protected ImageElement getImageElement(Path filePath, double durationInSeconds, int delay, int fps, boolean keepAfterEnd, boolean considerMargins) {
        return new ImageElement(
                filePath,
                new ElementPosition(height / 2, width / 2),
                durationInSeconds,
                delay,
                fps,
                keepAfterEnd,
                new ElementSize(width - ProjectProperties.Margins.LEFT - ProjectProperties.Margins.RIGHT, height - ProjectProperties.Margins.TOP - ProjectProperties.Margins.BOTTOM),
                considerMargins
        );
    }

    protected SceneMargin defaultSceneMargin() {
        return SceneMargin.builder()
                .top(ProjectProperties.Margins.TOP)
                .right(ProjectProperties.Margins.RIGHT)
                .bottom(ProjectProperties.Margins.BOTTOM)
                .left(ProjectProperties.Margins.LEFT)
                .build();
    }

    private SceneConfig createWelcomeScreen() {
        return new SceneBuilder()
                .setBackgroundColor(Color.WHITE)
                .setTextColor(Color.BLACK)
                .setWidth(width)
                .setHeight(height)
                .setDuration(getIntroDuration())
                .addElements(getIntroElements())
                .build();
    }

    private SceneConfig createThankYouScreen() {
        Set<String> thankYouNames = getUsernameToThankYou(articles);
        Path filePath = new ThankYouImageGenerator(thankYouNames, width, height).generateThankYouImage();

        return new SceneBuilder()
                .setBackgroundColor(Color.WHITE)
                .setTextColor(Color.BLACK)
                .setWidth(width)
                .setHeight(height)
                .addElement(getImageElement(filePath, 2, 0, frameRate, true, true))
                .addElement(ElementProvider.createSubscribeElement(width, height, frameRate))
                .setDuration(3)
                .build();
    }

    private Set<String> getUsernameToThankYou(List<Article> articles) {
        return articles.stream()
                .map(Article::getUserName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
