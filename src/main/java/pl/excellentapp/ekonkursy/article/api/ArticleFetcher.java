package pl.excellentapp.ekonkursy.article.api;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.article.models.ArticlePage;
import pl.excellentapp.ekonkursy.core.downloader.JsonDownloader;

import java.util.List;

@RequiredArgsConstructor
public class ArticleFetcher {

    private final JsonDownloader jsonDownloader;
    private final ArticleApiUrlProvider apiUrlProvider;

    public List<Article> lastAdded() {
        return fetchArticles(apiUrlProvider.getLastAddedUrl());
    }

    public List<Article> top(String type) {
        return fetchArticles(apiUrlProvider.getTopUrl(type));
    }

    public List<Article> end() {
        return fetchArticles(apiUrlProvider.getEndUrl());
    }

    public List<Article> search(String phrase) {
        return fetchArticles(apiUrlProvider.getSearchUrl(phrase), true);
    }

    private List<Article> fetchArticles(String url) {
        return fetchArticles(url, false);
    }

    private List<Article> fetchArticles(String url, boolean isPost) {
        ArticlePage response = isPost
                ? jsonDownloader.postJson(url, ArticlePage.class)
                : jsonDownloader.getJson(url, ArticlePage.class);

        return response != null && response.hasArticles()
                ? response.getData().stream().filter(Article::hasImage).toList()
                : List.of();
    }
}
