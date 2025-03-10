package pl.excellentapp.ekonkursy.projects;

import pl.excellentapp.ekonkursy.IVideoProjectConfig;
import pl.excellentapp.ekonkursy.VideoProjectConfig;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.core.ProjectProperties;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.image.ImageStripGenerator;
import pl.excellentapp.ekonkursy.image.ThankYouImageGenerator;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.builder.SceneBuilder;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;
import pl.excellentapp.ekonkursy.scene.effects.FadeEffect;
import pl.excellentapp.ekonkursy.scene.effects.ResizeEffect;
import pl.excellentapp.ekonkursy.scene.effects.ScrollingEffect;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ElementProvider;
import pl.excellentapp.ekonkursy.scene.elements.ElementSize;
import pl.excellentapp.ekonkursy.scene.elements.ImageElement;
import pl.excellentapp.ekonkursy.scene.elements.TextElement;

import java.awt.Color;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.excellentapp.ekonkursy.core.ProjectProperties.TEMPORARY_DIRECTORY;

public class TopOfMonthArticleVideoProjectConfig implements IVideoProjectConfig {

    private final ArticleImageDownloader imageDownloader;
    private final ImageProcessor imageProcessor;
    private final ImageStripGenerator imageStripGenerator;
    private final List<Article> articles;
    private final int width;
    private final int height;
    private final int frameRate;

    public TopOfMonthArticleVideoProjectConfig(ArticleImageDownloader imageDownloader, ImageProcessor imageProcessor, ArticleFetcher articleFetcher, ImageStripGenerator imageStripGenerator) {
        this.imageDownloader = imageDownloader;
        this.imageProcessor = imageProcessor;
        this.imageStripGenerator = imageStripGenerator;
        this.articles = articleFetcher.top("month");
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
                .addElement(new TextElement(
                        "Hot miesiąca",
                        new ElementPosition(height - 500, width / 2),
                        durationInSeconds,
                        0,
                        20,
                        new Color(0xB60C20),
                        frameRate,
                        new ElementSize(width, 100),
                        false,
                        List.of(new ResizeEffect(), new FadeEffect(1, 1, frameRate))
                ))
                .build();
    }

    private SceneConfig createListOfArticleScreen() {
        int displayDuration = articles.size();
        Color backgroundColor = Color.WHITE;
        Color textColor = Color.BLACK;
        SceneBuilder sceneBuilder = new SceneBuilder()
                .setWidth(width)
                .setHeight(height)
                .setBackgroundColor(backgroundColor)
                .setTextColor(textColor)
                .setSceneMargin(SceneMargin.builder()
                        .top(0)
                        .right(0)
                        .bottom(0)
                        .left(0)
                        .build())
                .setDuration(displayDuration);
        imageDownloader.downloadImages(articles);
        articles.forEach(article -> imageProcessor.applyBackground(article.getImageFile().toPath(), backgroundColor));
        try {
            File file = new File(TEMPORARY_DIRECTORY + "/imagestrip.jpg");
            imageStripGenerator.createFilmStrip(file.getPath());
            sceneBuilder.addElement(new ImageElement(
                    file.toPath(),
                    new ElementPosition(height / 2, width / 2),
                    displayDuration,
                    0,
                    frameRate,
                    true,
                    new ElementSize(width, height),
                    false,
                    List.of(new ScrollingEffect())
            ));
        } catch (Exception ignored) {

        }
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
                .addElement(ElementProvider.createSubscribeElement(width, height, frameRate))
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
                new ElementSize(width - ProjectProperties.Margins.LEFT - ProjectProperties.Margins.RIGHT, height - ProjectProperties.Margins.TOP - ProjectProperties.Margins.BOTTOM),
                true
        );
    }
}