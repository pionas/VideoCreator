package pl.excellentapp.ekonkursy.projects;

import pl.excellentapp.ekonkursy.article.api.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.service.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.config.ProjectProperties;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.image.ImageStripGenerator;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.builder.SceneBuilder;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;
import pl.excellentapp.ekonkursy.scene.effects.FadeEffect;
import pl.excellentapp.ekonkursy.scene.effects.ResizeEffect;
import pl.excellentapp.ekonkursy.scene.effects.ScrollingEffect;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ElementSize;
import pl.excellentapp.ekonkursy.scene.elements.ImageElement;
import pl.excellentapp.ekonkursy.scene.elements.SceneElement;
import pl.excellentapp.ekonkursy.scene.elements.TextElement;

import java.awt.Color;
import java.io.File;
import java.util.List;

import static pl.excellentapp.ekonkursy.config.ProjectProperties.TEMPORARY_DIRECTORY;

public class TopMonthlyArticleVideoProjectConfig extends AbstractArticleVideoProjectConfig {

    private final ImageStripGenerator imageStripGenerator;
    private final double durationInSeconds = 2.0;

    public TopMonthlyArticleVideoProjectConfig(ArticleImageDownloader imageDownloader, ImageProcessor imageProcessor, ArticleFetcher articleFetcher, ImageStripGenerator imageStripGenerator) {
        super(
                imageDownloader,
                imageProcessor,
                articleFetcher.top("month"),
                ProjectProperties.VideoSettings.WIDTH,
                ProjectProperties.VideoSettings.HEIGHT,
                ProjectProperties.VideoSettings.FRAME_RATE
        );
        this.imageStripGenerator = imageStripGenerator;
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
                )
        );
    }

    @Override
    protected SceneConfig createListOfArticleScreen() {
        int displayDuration = articles.size();
        Color backgroundColor = Color.WHITE;
        Color textColor = Color.BLACK;
        SceneBuilder sceneBuilder = new SceneBuilder()
                .setWidth(width)
                .setHeight(height)
                .setBackgroundColor(backgroundColor)
                .setTextColor(textColor)
                .setSceneMargin(noneSceneMargin())
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

    protected SceneMargin noneSceneMargin() {
        return SceneMargin.builder()
                .top(0)
                .right(0)
                .bottom(0)
                .left(0)
                .build();
    }

}