package pl.excellentapp.ekonkursy.projects;

import pl.excellentapp.ekonkursy.article.api.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.service.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.config.ProjectProperties;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.builder.SceneBuilder;
import pl.excellentapp.ekonkursy.scene.effects.FadeEffect;
import pl.excellentapp.ekonkursy.scene.effects.ResizeEffect;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ElementProvider;
import pl.excellentapp.ekonkursy.scene.elements.ElementSize;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;
import pl.excellentapp.ekonkursy.scene.elements.TextElement;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TopWeeklyArticleVideoProjectConfig extends AbstractArticleVideoProjectConfig {

    private final double durationInSeconds = 2.0;

    public TopWeeklyArticleVideoProjectConfig(ArticleImageDownloader imageDownloader, ImageProcessor imageProcessor, ArticleFetcher articleFetcher) {
        super(
                imageDownloader,
                imageProcessor,
                articleFetcher.top("week"),
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
                getImageElement(ProjectProperties.Images.WELCOME, durationInSeconds, 0, frameRate, true, true),
                new TextElement(
                        "Hot tygodnia",
                        new ElementPosition(height - 500, width / 2),
                        durationInSeconds,
                        0,
                        20,
                        new Color(0xB60C20),
                        frameRate,
                        new ElementSize(width, 100),
                        false,
                        List.of(new ResizeEffect(), new FadeEffect(1, 1, frameRate))
                )
        );
    }

    @Override
    protected SceneConfig createListOfArticleScreen() {
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
                .addElement(ElementProvider.createEffectElement(width, height, frameRate, displayDuration))
                .setDuration(displayDuration);
        imageDownloader.downloadImages(articles);
        articles.forEach(article -> imageProcessor.applyBackground(article.getImageFile().toPath(), backgroundColor));
        articles.forEach(article -> sceneBuilder.addElement(getImageElement(article.getImageFile().toPath(), 1, delay.getAndIncrement(), frameRate, false, true)));
        return sceneBuilder.build();
    }

}