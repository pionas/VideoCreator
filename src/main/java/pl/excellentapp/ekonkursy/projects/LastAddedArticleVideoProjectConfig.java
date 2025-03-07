package pl.excellentapp.ekonkursy.projects;

import pl.excellentapp.ekonkursy.IVideoProjectConfig;
import pl.excellentapp.ekonkursy.VideoProjectConfig;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.core.ProjectProperties;

import java.util.List;

public class LastAddedArticleVideoProjectConfig implements IVideoProjectConfig {

    private final List<Article> articles;
    private final int width;
    private final int height;
    private final int frameRate;

    public LastAddedArticleVideoProjectConfig(ArticleFetcher articleFetcher) {
        this.articles = articleFetcher.lastAdded();
        this.width = ProjectProperties.WIDTH;
        this.height = ProjectProperties.HEIGHT;
        this.frameRate = ProjectProperties.FRAME_RATE;
    }

    @Override
    public VideoProjectConfig toVideoProjectConfig() {
        return new VideoProjectConfig(
                ProjectProperties.OUTPUT_FILE,
                width,
                height,
                frameRate,
                List.of()
        );
    }
}