package pl.excellentapp.ekonkursy.article;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.core.JsonDownloader;
import pl.excellentapp.ekonkursy.core.ProjectProperties;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.article.models.ArticlesLastAdded;

import java.util.List;

@RequiredArgsConstructor
public class ArticleFetcher {

    private final JsonDownloader jsonDownloader;

    public List<Article> fetchArticles(String endpoint) {
        ArticlesLastAdded response = jsonDownloader.getJson(ProjectProperties.EKONKURSY_API_URL + endpoint, ArticlesLastAdded.class);
        if (response == null || !response.hasArticles()) {
            return List.of();
        }

        return response.getData().stream()
                .filter(Article::hasImage)
                .toList();
    }
}
