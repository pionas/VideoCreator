package pl.excellentapp.ekonkursy.video.screens;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.excellentapp.ekonkursy.video.ExecutionMode;

import java.io.File;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class VideoConfig {

    private final File file;
    private final boolean loop;
    private final ExecutionMode executionMode;
    private final Position position;

    public static VideoConfig blockingLoop(File file, Position position) {
        return new VideoConfig(file, true, ExecutionMode.BLOCKING, position);
    }

    public static VideoConfig nonBlockingLoop(File file, Position position) {
        return new VideoConfig(file, true, ExecutionMode.NON_BLOCKING, position);
    }

    public static VideoConfig blocking(File file, Position position) {
        return new VideoConfig(file, false, ExecutionMode.BLOCKING, position);
    }

    public static VideoConfig nonBlocking(File file, Position position) {
        return new VideoConfig(file, false, ExecutionMode.NON_BLOCKING, position);
    }

    public boolean isBlocking() {
        return this.executionMode == ExecutionMode.BLOCKING;
    }
}
