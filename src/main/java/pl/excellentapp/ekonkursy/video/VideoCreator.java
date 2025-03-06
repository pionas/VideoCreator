package pl.excellentapp.ekonkursy.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.core.DirectoryCleaner;
import pl.excellentapp.ekonkursy.core.FileDownloader;
import pl.excellentapp.ekonkursy.core.JsonDownloader;
import pl.excellentapp.ekonkursy.image.ImageFilterProcessor;
import pl.excellentapp.ekonkursy.article.ArticleImageProcessorService;

class VideoCreator {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        FrameProcessor frameProcessor = new FrameProcessor();
        FileDownloader fileDownloader = new FileDownloader();
        JsonDownloader jsonDownloader = new JsonDownloader(objectMapper);
        VideoRecorder videoRecorder = new VideoRecorder(frameProcessor);
        ImageFilterProcessor imageFilterProcessor = new ImageFilterProcessor();
        ArticleImageProcessorService articleImageProcessorService = new ArticleImageProcessorService(imageFilterProcessor);
        ArticleFetcher articleFetcher = new ArticleFetcher(jsonDownloader);
        ArticleImageDownloader imageDownloader = new ArticleImageDownloader(fileDownloader);
        DirectoryCleaner imageDirectoryCleaner = new DirectoryCleaner();

        ArticleVideoProjectConfig articleVideoProjectConfig = new ArticleVideoProjectConfig(articleFetcher, imageDownloader, articleImageProcessorService);
        VideoProjectConfig videoProjectConfig = articleVideoProjectConfig.toVideoProjectConfig();
        VideoCreatorFacade videoCreator = new VideoCreatorFacade(videoRecorder, imageDirectoryCleaner);
        videoCreator.createVideo(videoProjectConfig);
    }

}
