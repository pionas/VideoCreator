package pl.excellentapp.ekonkursy.video.screens;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
@Getter
public class ImageConfig {

    private final File file;
    private final int frames;
    private final int delayFrames;
    private final Position position;
    private ElementSize size;

    public ImageConfig(File file, int frames, Position position) {
        this.file = file;
        this.frames = frames;
        this.position = position;
        this.delayFrames = 0;
    }

    public boolean isBetween(int index) {
        return index >= (delayFrames) && index < (frames + delayFrames);
    }
}
