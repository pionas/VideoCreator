package pl.excellentapp.ekonkursy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.core.DirectoryCleaner;
import pl.excellentapp.ekonkursy.core.FileDownloader;
import pl.excellentapp.ekonkursy.core.JsonDownloader;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.image.ImageProcessorService;

class VideoCreator {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        FileDownloader fileDownloader = new FileDownloader();
        JsonDownloader jsonDownloader = new JsonDownloader(objectMapper);
        VideoRecorder videoRecorder = new VideoRecorder();
        ImageProcessor imageProcessor = new ImageProcessor();

        ArticleFetcher articleFetcher = new ArticleFetcher(jsonDownloader);
        ArticleImageDownloader imageDownloader = new ArticleImageDownloader(fileDownloader);
        ImageProcessorService imageProcessorService = new ImageProcessorService(imageProcessor);
        DirectoryCleaner imageDirectoryCleaner = new DirectoryCleaner();

        VideoCreatorFacade videoCreator = new VideoCreatorFacade(articleFetcher, imageDownloader, imageProcessorService, videoRecorder, imageDirectoryCleaner);
        videoCreator.createVideo();
    }

}
