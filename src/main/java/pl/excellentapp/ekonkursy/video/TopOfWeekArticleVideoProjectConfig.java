package pl.excellentapp.ekonkursy.video;

import pl.excellentapp.ekonkursy.MovieConfig;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.video.screens.ScreenFactory;

import java.util.List;

public class TopOfWeekArticleVideoProjectConfig implements IVideoProjectConfig {

    private final ScreenFactory screenFactory;
    private final List<Article> articles;
    private final int width;
    private final int height;
    private final int frameRate;

    public TopOfWeekArticleVideoProjectConfig(ArticleFetcher articleFetcher, ScreenFactory screenFactory) {
        this.screenFactory = screenFactory;
        this.articles = articleFetcher.fetchArticles("/api/statistics/article/top?type=week");
        this.width = MovieConfig.WIDTH;
        this.height = MovieConfig.HEIGHT;
        this.frameRate = MovieConfig.FRAME_RATE;
    }

    public VideoProjectConfig toVideoProjectConfig() {
        return new VideoProjectConfig(
                MovieConfig.OUTPUT_FILE,
                width,
                height,
                frameRate,
                screenFactory.createScreens(articles, width, height, frameRate)
        );
    }
}