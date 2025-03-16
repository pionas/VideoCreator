package pl.excellentapp.ekonkursy.projects;

import pl.excellentapp.ekonkursy.article.api.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.service.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.config.ProjectProperties;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.builder.SceneBuilder;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ElementProvider;
import pl.excellentapp.ekonkursy.scene.elements.ElementSize;
import pl.excellentapp.ekonkursy.scene.elements.ImageElement;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EndingTodayArticleVideoProjectConfig extends AbstractArticleVideoProjectConfig {

    private final double durationInSeconds = 2.0;

    public EndingTodayArticleVideoProjectConfig(ArticleImageDownloader imageDownloader, ImageProcessor imageProcessor, ArticleFetcher articleFetcher) {
        super(
                imageDownloader,
                imageProcessor,
                articleFetcher.end(),
                ProjectProperties.VideoSettings.WIDTH,
                ProjectProperties.VideoSettings.HEIGHT,
                ProjectProperties.VideoSettings.FRAME_RATE
        );
    }

    @Override
    protected double getIntroDuration() {
        return durationInSeconds;
    }

    @Override
    protected List<SceneElement> getIntroElements() {
        return List.of(
                ElementProvider.createLastChanceElement(width, height, frameRate, (int) durationInSeconds),
                new ImageElement(
                        ProjectProperties.Images.WELCOME_ENDING_TODAY,
                        new ElementPosition(height / 2, width / 2),
                        1 / 30.0,
                        0,
                        frameRate,
                        false,
                        new ElementSize(width , height),
                        false
                )
        );
    }

    @Override
    public SceneConfig createListOfArticleScreen() {
        AtomicInteger delay = new AtomicInteger();
        int displayDuration = articles.size();
        Color backgroundColor = Color.WHITE;
        Color textColor = Color.BLACK;
        SceneBuilder sceneBuilder = new SceneBuilder()
                .setWidth(width)
                .setHeight(height)
                .setBackgroundColor(backgroundColor)
                .setTextColor(textColor)
                .setSceneMargin(defaultSceneMargin())
                .addElement(ElementProvider.createFluidGradientElement(width, height, frameRate, displayDuration))
                .setDuration(displayDuration);
        imageDownloader.downloadImages(articles);
        articles.forEach(article -> imageProcessor.applyBackground(article.getImageFile().toPath(), backgroundColor));
        articles.forEach(article -> sceneBuilder.addElement(getImageElement(article.getImageFile().toPath(), 1, delay.getAndIncrement(), frameRate, false, true)));
        return sceneBuilder.build();
    }
}