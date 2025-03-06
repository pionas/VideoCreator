package pl.excellentapp.ekonkursy.core;

import java.awt.Font;
import java.io.File;

public class FontLoader {

    public Font loadFont(String path, int size) {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(path)).deriveFont((float) size);
        } catch (Exception e) {
            System.err.println("Błąd ładowania czcionki: " + e.getMessage());
            return new Font("Arial", Font.BOLD, size);
        }
    }
}
