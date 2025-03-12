package pl.excellentapp.ekonkursy.utills;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.article.api.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.service.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.config.IVideoProjectConfig;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.image.ImageStripGenerator;
import pl.excellentapp.ekonkursy.projects.LastAddedArticleVideoProjectConfig;
import pl.excellentapp.ekonkursy.projects.EndingTodayArticleVideoProjectConfig;
import pl.excellentapp.ekonkursy.projects.TopMonthlyArticleVideoProjectConfig;
import pl.excellentapp.ekonkursy.projects.TopWeeklyArticleVideoProjectConfig;
import pl.excellentapp.ekonkursy.projects.WomensDayArticleVideoProjectConfig;

@RequiredArgsConstructor
public class VideoProjectLoader {

    private final ArticleImageDownloader imageDownloader;
    private final ImageProcessor imageProcessor;
    private final ArticleFetcher articleFetcher;
    private final ImageStripGenerator imageStripGenerator;

    public IVideoProjectConfig loadProject(String videoType) {
        return switch (videoType) {
            case "lastAdded" -> new LastAddedArticleVideoProjectConfig(imageDownloader, imageProcessor, articleFetcher);
            case "topWeekly" -> new TopWeeklyArticleVideoProjectConfig(imageDownloader, imageProcessor, articleFetcher);
            case "womensDay" -> new WomensDayArticleVideoProjectConfig(imageDownloader, imageProcessor, articleFetcher);
            case "topMonthly" ->
                    new TopMonthlyArticleVideoProjectConfig(imageDownloader, imageProcessor, articleFetcher, imageStripGenerator);
            case "endingToday" ->
                    new EndingTodayArticleVideoProjectConfig(imageDownloader, imageProcessor, articleFetcher);
            default -> throw new IllegalArgumentException("Unknown project type: " + videoType);
        };
    }
}
