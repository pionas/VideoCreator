package pl.excellentapp.ekonkursy.core.cleanup;

import pl.excellentapp.ekonkursy.config.ProjectProperties;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryCleaner {

    public void clean() {
        Path directory = ProjectProperties.TEMPORARY_DIRECTORY;

        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException ignored) {
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }
}

