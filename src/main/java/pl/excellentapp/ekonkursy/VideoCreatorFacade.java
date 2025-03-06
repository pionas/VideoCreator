package pl.excellentapp.ekonkursy;

import lombok.RequiredArgsConstructor;
import org.bytedeco.opencv.opencv_core.Scalar;
import pl.excellentapp.ekonkursy.image.ImageProcessor;
import pl.excellentapp.ekonkursy.image.filters.BorderFilter;
import pl.excellentapp.ekonkursy.image.filters.ImageFilter;
import pl.excellentapp.ekonkursy.image.filters.ResizeFilter;
import pl.excellentapp.ekonkursy.models.Article;
import pl.excellentapp.ekonkursy.models.ArticlesLastAdded;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class VideoCreatorFacade {

    public static final String IMAGE_DIRECTORY = "images";

    private final FileDownloader fileDownloader;
    private final JsonDownloader jsonDownloader;
    private final ImageProcessor imageProcessor;
    private final VideoRecorder videoRecorder;

    public void createVideo() {
        List<Article> articles = fetchArticles();
        if (articles.isEmpty()) {
            System.out.println("Brak artykułów do przetworzenia.");
            return;
        }

        downloadArticleImages(articles);
        processArticleImages(articles);
        Set<String> thankYouNames = getUsernameToThankYou(articles);
        videoRecorder.recordVideo(articles, VideoConfig.OUTPUT_FILE, VideoConfig.WIDTH, VideoConfig.HEIGHT, VideoConfig.FRAME_RATE, thankYouNames);

        cleanImageDirectory();
    }

    private List<Article> fetchArticles() {
        ArticlesLastAdded response = jsonDownloader.getJson(VideoConfig.EKONKURSY_API_URL + "/api/articles/lastAdded", ArticlesLastAdded.class);
        if (response == null || !response.hasArticles()) {
            return List.of();
        }

        return response.getData().stream()
                .filter(Article::hasImage)
                .toList();
    }

    private void downloadArticleImages(List<Article> articles) {
        articles.forEach(article -> {
            File downloadedFile = fileDownloader.downloadFile(article.getImageUrl(VideoConfig.EKONKURSY_API_URL), article.getFileName());
            article.addFile(downloadedFile);
        });
    }

    private void processArticleImages(List<Article> articles) {
        List<ImageFilter> filters = Arrays.asList(
                new ResizeFilter(VideoConfig.WIDTH, VideoConfig.HEIGHT, VideoConfig.BACKGROUND_COLOR_BLACK),
                new BorderFilter(20, new Scalar(126, 126, 126, 126) )
        );
        articles.forEach(article -> {
            File processedFile = imageProcessor.processImage(article.getImageFile(), filters);
            article.addFile(processedFile);
        });
    }

    private void cleanImageDirectory() {
        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Katalog obrazów nie istnieje lub nie jest katalogiem.");
            return;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.delete()) {
                    System.out.println("Usunięto plik: " + file.getAbsolutePath());
                } else {
                    System.err.println("Nie udało się usunąć pliku: " + file.getAbsolutePath());
                }
            }
        }
    }

    private Set<String> getUsernameToThankYou(List<Article> articles) {
        return articles.stream()
                .map(Article::getUserName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
