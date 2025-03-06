package pl.excellentapp.ekonkursy;

import java.io.File;

public class DirectoryCleaner {

    public void clean() {
        File directory = new File(VideoConfig.TEMPORARY_DIRECTORY);
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
}
