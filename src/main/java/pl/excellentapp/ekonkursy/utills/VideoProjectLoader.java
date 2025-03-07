package pl.excellentapp.ekonkursy.utills;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.IVideoProjectConfig;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageService;
import pl.excellentapp.ekonkursy.projects.LastAddedArticleVideoProjectConfig;
import pl.excellentapp.ekonkursy.projects.TodayFinishArticleVideoProjectConfig;
import pl.excellentapp.ekonkursy.projects.TopOfMonthArticleVideoProjectConfig;
import pl.excellentapp.ekonkursy.projects.TopOfWeekArticleVideoProjectConfig;

@RequiredArgsConstructor
public class VideoProjectLoader {

    private final ArticleImageService imageService;
    private final ArticleFetcher articleFetcher;

    public IVideoProjectConfig loadProject(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("No project type provided.");
        }
        return switch (args[0]) {
            case "lastAdded" -> new LastAddedArticleVideoProjectConfig(articleFetcher);
            case "topWeek" -> new TopOfWeekArticleVideoProjectConfig(articleFetcher);
            case "topMonth" -> new TopOfMonthArticleVideoProjectConfig(articleFetcher);
            case "todayFinish" -> new TodayFinishArticleVideoProjectConfig(imageService, articleFetcher);
            default -> throw new IllegalArgumentException("Unknown project type: " + args[0]);
        };
    }
}
