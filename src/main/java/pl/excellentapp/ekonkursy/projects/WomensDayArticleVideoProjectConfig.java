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
import java.util.concurrent.atomic.AtomicInteger;

public class WomensDayArticleVideoProjectConfig extends AbstractArticleVideoProjectConfig {

    public WomensDayArticleVideoProjectConfig(ArticleImageDownloader imageDownloader, ImageProcessor imageProcessor, ArticleFetcher articleFetcher) {
        super(
                imageDownloader,
                imageProcessor,
                articleFetcher.search("konkursy dzień kobiet"),
                ProjectProperties.VideoSettings.WIDTH,
                ProjectProperties.VideoSettings.HEIGHT,
                ProjectProperties.VideoSettings.FRAME_RATE
        );
    }

    @Override
    protected double getIntroDuration() {
        return 2.0;
    }

    @Override
    protected List<SceneElement> getIntroElements() {
        return List.of(
                ElementProvider.createWomensDayElement(width, height, frameRate)
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
                .addElement(ElementProvider.createConfettiElement(width, height, frameRate))
                .setDuration(displayDuration);
        imageDownloader.downloadImages(articles);
        articles.forEach(article -> imageProcessor.applyBackground(article.getImageFile().toPath(), backgroundColor));
        articles.forEach(article -> sceneBuilder.addElement(getImageElement(article.getImageFile().toPath(), 1, delay.getAndIncrement(), frameRate, false, true)));
        return sceneBuilder.build();
    }

}