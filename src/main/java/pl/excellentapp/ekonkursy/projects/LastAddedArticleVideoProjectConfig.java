package pl.excellentapp.ekonkursy.projects;

import pl.excellentapp.ekonkursy.article.api.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.service.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.config.ProjectProperties;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.builder.SceneBuilder;
import pl.excellentapp.ekonkursy.scene.elements.ElementProvider;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class LastAddedArticleVideoProjectConfig extends AbstractArticleVideoProjectConfig {

    private static final Random RANDOM = new Random();

    public LastAddedArticleVideoProjectConfig(ArticleImageDownloader imageDownloader, ImageProcessor imageProcessor, ArticleFetcher articleFetcher) {
        super(
                imageDownloader,
                imageProcessor,
                articleFetcher.lastAdded(),
                ProjectProperties.VideoSettings.WIDTH,
                ProjectProperties.VideoSettings.HEIGHT,
                ProjectProperties.VideoSettings.FRAME_RATE
        );
    }

    @Override
    protected double getIntroDuration() {
        return 8;
    }

    @Override
    protected List<SceneElement> getIntroElements() {
        return List.of(
                ElementProvider.createIntroLastAddedElement(width, height, frameRate)
        );
    }

    @Override
    protected SceneConfig createListOfArticleScreen() {
        AtomicInteger delay = new AtomicInteger();
        int displayDuration = articles.size();
        Color backgroundColor = Color.WHITE;
        Color textColor = Color.BLACK;
        SceneElement sceneElement = RANDOM.nextBoolean() ? ElementProvider.createEffectElement(width, height, frameRate, displayDuration) : ElementProvider.createBackgroundStarsElement(width, height, frameRate);
        SceneBuilder sceneBuilder = new SceneBuilder()
                .setWidth(width)
                .setHeight(height)
                .setBackgroundColor(backgroundColor)
                .setTextColor(textColor)
                .setSceneMargin(defaultSceneMargin())
                .addElement(sceneElement)
                .setDuration(displayDuration);
        imageDownloader.downloadImages(articles);
        articles.forEach(article -> imageProcessor.applyBackground(article.getImageFile().toPath(), backgroundColor));
        articles.forEach(article -> sceneBuilder.addElement(getImageElement(article.getImageFile().toPath(), 1, delay.getAndIncrement(), frameRate, false, true)));
        return sceneBuilder.build();
    }

}