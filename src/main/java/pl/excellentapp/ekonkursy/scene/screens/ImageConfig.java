package pl.excellentapp.ekonkursy.scene.screens;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.excellentapp.ekonkursy.scene.elements.ElementPosition;
import pl.excellentapp.ekonkursy.scene.elements.ElementSize;

import java.io.File;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ImageConfig {

    private final File file;
    private final int frames;
    private final int delayFrames;
    private final ElementPosition elementPosition;
    private ElementSize size;

    public boolean isBetween(int index) {
        return index >= (delayFrames) && index < (frames + delayFrames);
    }

    public boolean hasAfter(int index) {
        return (delayFrames + frames) > index;
    }
}
