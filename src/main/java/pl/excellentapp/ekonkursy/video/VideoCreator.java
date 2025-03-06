package pl.excellentapp.ekonkursy.video;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.excellentapp.ekonkursy.MovieConfig;
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

        FrameProcessor frameProcessor = new FrameProcessor();
        FileDownloader fileDownloader = new FileDownloader();
        JsonDownloader jsonDownloader = new JsonDownloader(objectMapper);
        VideoRecorder videoRecorder = new VideoRecorder(frameProcessor);
        ImageProcessor imageProcessor = new ImageProcessor();
        ImageProcessorService imageProcessorService = new ImageProcessorService(imageProcessor);
        ArticleFetcher articleFetcher = new ArticleFetcher(jsonDownloader);
        ArticleImageDownloader imageDownloader = new ArticleImageDownloader(fileDownloader);
        DirectoryCleaner imageDirectoryCleaner = new DirectoryCleaner();

        VideoCreatorFacade videoCreator = new VideoCreatorFacade(articleFetcher, imageDownloader, imageProcessorService, videoRecorder, imageDirectoryCleaner);
        videoCreator.createVideo(MovieConfig.OUTPUT_FILE, MovieConfig.WIDTH, MovieConfig.HEIGHT, MovieConfig.FRAME_RATE);
    }

}
