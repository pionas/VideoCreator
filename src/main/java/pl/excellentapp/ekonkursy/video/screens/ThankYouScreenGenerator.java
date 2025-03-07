package pl.excellentapp.ekonkursy.video.screens;

import pl.excellentapp.ekonkursy.core.ProjectProperties;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.image.ThankYouImageGenerator;
import pl.excellentapp.ekonkursy.video.ExecutionMode;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ThankYouScreenGenerator {

    public Screen createThankYouScreen(List<Article> articles, int width, int height, int frameRate) {
        Set<String> thankYouNames = getUsernameToThankYou(articles);
        File file = new ThankYouImageGenerator(thankYouNames, width, height).generateThankYouImage();

        return ImageMovieScreen.builder()
                .targetWidth(width)
                .targetHeight(height)
                .background(ProjectProperties.BACKGROUND_COLOR_WHITE)
                .images(List.of(ImageConfig.builder()
                        .file(file)
                        .frames(frameRate * 3)
                        .position(new Position(height / 2, width / 2))
                        .build()))
                .videos(List.of(
                        VideoConfig.builder()
                                .file(new File(ProjectProperties.SUBSCRIBE_FILE))
                                .loop(true)
                                .executionMode(ExecutionMode.NON_BLOCKING)
                                .position(new Position(height - 300, width / 2))
                                .size(ElementSize.builder().maxWidth(200).maxHeight(200).build())
                                .order(Order.END)
                                .delayFrames(0)
                                .build()
                ))
                .maxFrames(frameRate * 3)
                .build();
    }

    private Set<String> getUsernameToThankYou(List<Article> articles) {
        return articles.stream()
                .map(Article::getUserName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
