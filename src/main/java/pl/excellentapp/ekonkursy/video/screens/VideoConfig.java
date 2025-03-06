package pl.excellentapp.ekonkursy.video.screens;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bytedeco.javacv.Frame;
import pl.excellentapp.ekonkursy.video.ExecutionMode;

import java.io.File;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class VideoConfig {

    private final File file;
    private final boolean loop;
    private final ExecutionMode executionMode;
    private final Position position;
    private final int delayFrames;
    @Setter
    private Frame lastEffect;

    public static VideoConfig blockingLoop(File file, Position position) {
        return new VideoConfig(file, true, ExecutionMode.BLOCKING, position, 0);
    }

    public static VideoConfig nonBlockingLoop(File file, Position position) {
        return new VideoConfig(file, true, ExecutionMode.NON_BLOCKING, position, 0);
    }

    public static VideoConfig blocking(File file, Position position) {
        return new VideoConfig(file, false, ExecutionMode.BLOCKING, position, 0);
    }

    public static VideoConfig nonBlocking(File file, Position position) {
        return new VideoConfig(file, false, ExecutionMode.NON_BLOCKING, position, 0);
    }

    public static VideoConfig blockingLoop(File file, Position position, int delay) {
        return new VideoConfig(file, true, ExecutionMode.BLOCKING, position, delay);
    }

    public static VideoConfig nonBlockingLoop(File file, Position position, int delay) {
        return new VideoConfig(file, true, ExecutionMode.NON_BLOCKING, position, delay);
    }

    public static VideoConfig blocking(File file, Position position, int delay) {
        return new VideoConfig(file, false, ExecutionMode.BLOCKING, position, delay);
    }

    public static VideoConfig nonBlocking(File file, Position position, int delay) {
        return new VideoConfig(file, false, ExecutionMode.NON_BLOCKING, position, delay);
    }

    public boolean isBlocking() {
        return this.executionMode == ExecutionMode.BLOCKING;
    }

    public boolean hasEffect() {
        return Objects.nonNull(lastEffect);
    }

    public boolean isDelay(int index) {
        return delayFrames > index;
    }
}
