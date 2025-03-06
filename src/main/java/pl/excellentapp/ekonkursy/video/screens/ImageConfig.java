package pl.excellentapp.ekonkursy.video.screens;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.File;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ImageConfig {

    private final File file;
    private final int frames;
    private final int delayFrames;
    private final Position position;
    private ElementSize size;

    public boolean isBetween(int index) {
        return index >= (delayFrames) && index < (frames + delayFrames);
    }

    public boolean hasAfter(int index) {
        return (delayFrames + frames) > index;
    }
}
