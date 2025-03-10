package pl.excellentapp.ekonkursy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import pl.excellentapp.ekonkursy.article.ArticleApiUrlProvider;
import pl.excellentapp.ekonkursy.article.ArticleFetcher;
import pl.excellentapp.ekonkursy.article.ArticleImageDownloader;
import pl.excellentapp.ekonkursy.core.DirectoryCleaner;
import pl.excellentapp.ekonkursy.core.FileDownloader;
import pl.excellentapp.ekonkursy.core.JsonDownloader;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.image.ImageStripGenerator;
import pl.excellentapp.ekonkursy.scene.SceneRenderer;
import pl.excellentapp.ekonkursy.utills.VideoProjectLoader;

public class VideoCreator {

    public static void main(String[] args) {
        VideoProjectLoader videoProjectLoader = new VideoProjectLoader(
                getArticleImageDownloader(),
                getImageProcessor(),
                getArticleFetcher(),
                new ImageStripGenerator()
        );

        VideoProjectConfig videoProjectConfig = videoProjectLoader.loadProject(args).toVideoProjectConfig();
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
