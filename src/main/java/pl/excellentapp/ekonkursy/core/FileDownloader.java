package pl.excellentapp.ekonkursy.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileDownloader {

    public File downloadFile(String fileUrl, String destinationPath) {
        try {
            URI uri = URI.create(fileUrl);
            URL url = uri.toURL();
            Path destination = Path.of(destinationPath);
            if (destination.getParent() != null) {
                Files.createDirectories(destination.getParent());
            }
            try (InputStream in = url.openStream()) {
                Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return destination.toFile();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to download file from " + fileUrl + " to " + destinationPath, ex);
        }
    }
}


