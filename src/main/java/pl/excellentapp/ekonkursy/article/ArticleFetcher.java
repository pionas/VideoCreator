package pl.excellentapp.ekonkursy.article;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.article.models.ArticlePage;
import pl.excellentapp.ekonkursy.core.JsonDownloader;
import pl.excellentapp.ekonkursy.core.ProjectProperties;

import java.util.List;

@RequiredArgsConstructor
public class ArticleFetcher {

    private static final String ARTICLE_LAST_ADDED_PATH = "/api/articles/lastAdded";
    private static final String ARTICLE_TOP_PATH = "/api/statistics/article/top?type=";
    private static final String ARTICLE_END_PATH = "/api/statistics/article/end-today";
    private final JsonDownloader jsonDownloader;

    public List<Article> lastAdded() {
        ArticlePage response = jsonDownloader.getJson(ProjectProperties.EKONKURSY_API_URL + ARTICLE_LAST_ADDED_PATH, ArticlePage.class);
        if (response == null || !response.hasArticles()) {
            return List.of();
        }
        return response.getData().stream()
                .filter(Article::hasImage)
                .toList();
    }

    public List<Article> top(String type) {
        ArticlePage response = jsonDownloader.getJson(ProjectProperties.EKONKURSY_API_URL + ARTICLE_TOP_PATH + type, ArticlePage.class);
        if (response == null || !response.hasArticles()) {
            return List.of();
        }
        return response.getData().stream()
                .filter(Article::hasImage)
                .toList();
    }

    public List<Article> end() {
        ArticlePage response = jsonDownloader.getJson(ProjectProperties.EKONKURSY_API_URL + ARTICLE_END_PATH, ArticlePage.class);
        if (response == null || !response.hasArticles()) {
            return List.of();
        }
        return response.getData().stream()
                .filter(Article::hasImage)
                .toList();
    }
}
