package pl.excellentapp.ekonkursy.article.service;

import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.core.downloader.FileDownloader;
import pl.excellentapp.ekonkursy.config.ProjectProperties;
import pl.excellentapp.ekonkursy.article.models.Article;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public class ArticleImageDownloader {

    private final FileDownloader fileDownloader;

    public void downloadImages(List<Article> articles) {
        articles.forEach(article -> {
            File downloadedFile = fileDownloader.downloadFile(article.getImageUrl(ProjectProperties.EKONKURSY_API_URL), article.getFileName());
            article.addFile(downloadedFile);
        });
    }
}
