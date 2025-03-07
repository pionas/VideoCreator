package pl.excellentapp.ekonkursy;

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
import pl.excellentapp.ekonkursy.utills.VideoProjectLoader;

public class VideoCreator {

    public static void main(String[] args) {
        VideoProjectLoader videoProjectLoader = new VideoProjectLoader(
                getArticleImageService(),
                getArticleFetcher()
        );
        VideoCreatorFacade videoCreatorFacade = new VideoCreatorFacade(
                getVideoRecorder(),
                new DirectoryCleaner()
        );
        IVideoProjectConfig projectConfig = videoProjectLoader.loadProject(args);
        videoCreatorFacade.createVideo(projectConfig.toVideoProjectConfig());
        System.out.println("Generowanie wideo zakończone!");
    }

    private static ArticleImageService getArticleImageService() {
        ArticleImageDownloader imageDownloader = getArticleImageDownloader();
        ArticleImageProcessorService articleImageProcessorService = getArticleImageProcessorService();

        return new ArticleImageService(imageDownloader, articleImageProcessorService);
    }

    private static VideoRecorder getVideoRecorder() {
        FrameProcessor frameProcessor = new FrameProcessor();
        return new VideoRecorder(frameProcessor);
    }

    private static ArticleImageProcessorService getArticleImageProcessorService() {
        ImageFilterProcessor imageFilterProcessor = new ImageFilterProcessor();
        return new ArticleImageProcessorService(imageFilterProcessor);
    }

    private static ArticleImageDownloader getArticleImageDownloader() {
        FileDownloader fileDownloader = new FileDownloader();
        return new ArticleImageDownloader(fileDownloader);
    }

    private static ArticleFetcher getArticleFetcher() {
        JsonDownloader jsonDownloader = getJsonDownloader();
        return new ArticleFetcher(jsonDownloader);
    }

    private static JsonDownloader getJsonDownloader() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new JsonDownloader(objectMapper);
    }
}
