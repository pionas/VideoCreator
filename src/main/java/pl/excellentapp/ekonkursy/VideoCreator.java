package pl.excellentapp.ekonkursy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.excellentapp.ekonkursy.article.api.ArticleApiUrlProvider;
import pl.excellentapp.ekonkursy.article.api.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.service.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.config.VideoProjectConfig;
import pl.excellentapp.ekonkursy.core.cleanup.DirectoryCleaner;
import pl.excellentapp.ekonkursy.core.downloader.FileDownloader;
import pl.excellentapp.ekonkursy.core.downloader.JsonDownloader;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.image.ImageStripGenerator;
import pl.excellentapp.ekonkursy.scene.SceneRenderer;
import pl.excellentapp.ekonkursy.utills.VideoProjectLoader;

public class VideoCreator {

    public static void main(String[] args) {
        String videoType = (args.length > 1) ? args[1] : args[0];
        VideoProjectLoader videoProjectLoader = new VideoProjectLoader(
                getArticleImageDownloader(),
                getImageProcessor(),
                getArticleFetcher(),
                new ImageStripGenerator()
        );

        VideoProjectConfig videoProjectConfig = videoProjectLoader.loadProject(videoType).toVideoProjectConfig();
        SceneRenderer sceneRenderer = new SceneRenderer(videoProjectConfig.getFrameRate(), videoProjectConfig.getWidth(), videoProjectConfig.getHeight());
        sceneRenderer.renderScenes(videoProjectConfig.getSceneConfigs(), "./movie.mp4");
        new DirectoryCleaner().clean();
    }

    private static ArticleImageDownloader getArticleImageDownloader() {
        FileDownloader fileDownloader = new FileDownloader();
        return new ArticleImageDownloader(fileDownloader);
    }

    private static ImageProcessor getImageProcessor() {
        return new ImageProcessor();
    }

    private static ArticleFetcher getArticleFetcher() {
        JsonDownloader jsonDownloader = getJsonDownloader();
        return new ArticleFetcher(jsonDownloader, new ArticleApiUrlProvider());
    }

    private static JsonDownloader getJsonDownloader() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new JsonDownloader(objectMapper);
    }
}
