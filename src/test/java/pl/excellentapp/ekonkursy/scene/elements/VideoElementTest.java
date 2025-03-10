package pl.excellentapp.ekonkursy.scene.elements;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.excellentapp.ekonkursy.scene.SceneConfig;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;

import java.awt.Color;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VideoElementTest {

    private final ElementPosition mockPosition = mock(ElementPosition.class);
    private final Path file = new File("./movies/effect.mp4").toPath();
    private final FFmpegFrameGrabber mockGrabber = mock(FFmpegFrameGrabber.class);

    @BeforeEach
    void setUp() throws Exception {
        when(mockPosition.getLeft()).thenReturn(10);
        when(mockPosition.getTop()).thenReturn(20);
        when(mockGrabber.getLengthInFrames()).thenReturn(10);
        when(mockGrabber.grabImage()).thenReturn(mock(Frame.class));
    }

    @ParameterizedTest
    @MethodSource("provideVideoElement")
    void testRenderWithinTimeFrame(int currentFrame, int displayDuration, int delay, int fps, boolean loop, boolean keepLastFrame, int fetchTimes) {
        // given
        VideoElement videoElement = spy(new VideoElement(file, mockPosition, displayDuration, delay, fps, loop, keepLastFrame, new ElementSize(100, 100), false));
        SceneConfig sceneConfig = new SceneConfig(100, 100, Color.WHITE, Color.BLACK, 1, SceneMargin.builder().top(0).right(0).bottom(0).left(0).build(), List.of());
        Mat mockFrame = new Mat(100, 100, 16);

        // when
        videoElement.render(sceneConfig, mockFrame, currentFrame);

        // then
        verify(videoElement, times(fetchTimes)).addToVideoFrame(any(), any(), any(), any());
    }

    private static Stream<Arguments> provideVideoElement() {
        return Stream.of(
                Arguments.of(0, 1, 0, 10, true, true, 1),
                Arguments.of(0, 1, 0, 10, false, true, 1),
                Arguments.of(0, 1, 0, 10, true, false, 1),
                Arguments.of(0, 1, 0, 10, false, false, 1),
                Arguments.of(0, 1, 5, 10, true, true, 0),
                Arguments.of(0, 1, 5, 10, false, true, 0),
                Arguments.of(0, 1, 5, 10, true, false, 0),
                Arguments.of(0, 1, 5, 10, false, false, 0),
                Arguments.of(10, 1, 0, 10, true, true, 1),
                Arguments.of(10, 1, 0, 10, false, true, 1),
                Arguments.of(10, 1, 0, 10, true, false, 1),
                Arguments.of(10, 1, 0, 10, false, false, 1),
                Arguments.of(10, 1, 1, 10, true, true, 1),
                Arguments.of(10, 1, 1, 10, false, true, 1),
                Arguments.of(10, 1, 1, 10, true, false, 1),
                Arguments.of(10, 1, 1, 10, false, false, 1),
                Arguments.of(20, 1, 0, 10, true, true, 1),
                Arguments.of(20, 1, 0, 10, false, true, 1),
                Arguments.of(20, 1, 0, 10, true, false, 1),
                Arguments.of(20, 1, 0, 10, false, false, 0)
        );
    }
}
