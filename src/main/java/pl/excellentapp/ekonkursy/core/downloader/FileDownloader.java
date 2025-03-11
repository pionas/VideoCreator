package pl.excellentapp.ekonkursy.core.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;

public class FileDownloader {

    private static final ConcurrentHashMap<String, File> cache = new ConcurrentHashMap<>();

    public File downloadFile(String fileUrl, String destinationPath) {
        Path destination = Path.of(destinationPath);
        if (cache.containsKey(fileUrl) && Files.exists(destination)) {
            return cache.get(fileUrl);
        }
        try {
            URI uri = URI.create(fileUrl);
            URL url = uri.toURL();

            if (destination.getParent() != null) {
                Files.createDirectories(destination.getParent());
            }
            try (InputStream in = url.openStream()) {
                Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            File downloadedFile = destination.toFile();
            cache.put(fileUrl, downloadedFile);
            return downloadedFile;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to download file from " + fileUrl + " to " + destinationPath, ex);
        }
    }
}


