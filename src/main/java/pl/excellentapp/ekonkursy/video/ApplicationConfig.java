package pl.excellentapp.ekonkursy.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.article.ArticleImageProcessorService;
import pl.excellentapp.ekonkursy.article.ArticleImageService;
import pl.excellentapp.ekonkursy.core.DirectoryCleaner;
import pl.excellentapp.ekonkursy.core.FileDownloader;
import pl.excellentapp.ekonkursy.core.JsonDownloader;
import pl.excellentapp.ekonkursy.image.ImageFilterProcessor;
import pl.excellentapp.ekonkursy.video.screens.ScreenFactory;
import pl.excellentapp.ekonkursy.video.screens.ThankYouScreenGenerator;

public class ApplicationConfig {

    private final ArticleVideoProjectConfig articleVideoProjectConfig;
    private final VideoCreatorFacade videoCreator;

    public ApplicationConfig() {
        this.articleVideoProjectConfig = new ArticleVideoProjectConfig(
                getArticleFetcher(),
                new ScreenFactory(new ThankYouScreenGenerator(), getArticleImageService())
        );
        this.videoCreator = new VideoCreatorFacade(
                getVideoRecorder(),
                new DirectoryCleaner()
        );
    }

    public VideoCreatorFacade getVideoCreatorFacade() {
        return videoCreator;
    }

    public VideoProjectConfig getVideoProjectConfig() {
        return articleVideoProjectConfig.toVideoProjectConfig();
    }

    private ArticleImageService getArticleImageService() {
        ArticleImageDownloader imageDownloader = getArticleImageDownloader();
        ArticleImageProcessorService articleImageProcessorService = getArticleImageProcessorService();

        return new ArticleImageService(imageDownloader, articleImageProcessorService);
    }

    private VideoRecorder getVideoRecorder() {
        FrameProcessor frameProcessor = new FrameProcessor();
        return new VideoRecorder(frameProcessor);
    }

    private ArticleImageProcessorService getArticleImageProcessorService() {
        ImageFilterProcessor imageFilterProcessor = new ImageFilterProcessor();
        return new ArticleImageProcessorService(imageFilterProcessor);
    }

    private ArticleImageDownloader getArticleImageDownloader() {
        FileDownloader fileDownloader = new FileDownloader();
        return new ArticleImageDownloader(fileDownloader);
    }

    private ArticleFetcher getArticleFetcher() {
        JsonDownloader jsonDownloader = getJsonDownloader();
        return new ArticleFetcher(jsonDownloader);
    }

    private JsonDownloader getJsonDownloader() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new JsonDownloader(objectMapper);
    }
}
