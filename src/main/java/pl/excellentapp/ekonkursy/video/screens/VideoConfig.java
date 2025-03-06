package pl.excellentapp.ekonkursy.video.screens;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bytedeco.javacv.Frame;
import pl.excellentapp.ekonkursy.video.ExecutionMode;

import java.io.File;
import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class VideoConfig {

    private final File file;
    private final boolean loop;
    private final Order order;
    private final ExecutionMode executionMode;
    private final Position position;
    private final ElementSize size;
    private final int delayFrames;

    @Setter
    private Frame lastEffect;

    public boolean hasEffect() {
        return Objects.nonNull(lastEffect);
    }

    public boolean isDelay(int index) {
        return delayFrames > index;
    }

    public boolean isBefore() {
        return Order.START == order;
    }
}
