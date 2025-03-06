package pl.excellentapp.ekonkursy.article;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.JsonDownloader;
import pl.excellentapp.ekonkursy.VideoConfig;
import pl.excellentapp.ekonkursy.article.models.Article;
import pl.excellentapp.ekonkursy.article.models.ArticlesLastAdded;

import java.util.List;

@RequiredArgsConstructor
public class ArticleFetcher {

    private final JsonDownloader jsonDownloader;

    public List<Article> fetchArticles() {
        ArticlesLastAdded response = jsonDownloader.getJson(VideoConfig.EKONKURSY_API_URL + "/api/articles/lastAdded", ArticlesLastAdded.class);
        if (response == null || !response.hasArticles()) {
            return List.of();
        }

        return response.getData().stream()
                .filter(Article::hasImage)
                .toList();
    }
}
