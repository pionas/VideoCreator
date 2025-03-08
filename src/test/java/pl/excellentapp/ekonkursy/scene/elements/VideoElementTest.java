package pl.excellentapp.ekonkursy.scene.elements;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;

import java.io.File;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
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
    void testRenderWithinTimeFrame(int currentFrame, int displayDuration, int delay, int fps, boolean loop, boolean keepLastFrame) {
        // given
        VideoElement videoElement = new VideoElement(file, mockPosition, displayDuration, delay, fps, loop, keepLastFrame, new Size(100, 100), false);
        SceneMargin sceneMargin = SceneMargin.builder().build();
        Mat mockFrame = new Mat(100, 100, 16);

        // when
        assertDoesNotThrow(() -> videoElement.render(sceneMargin, mockFrame, currentFrame));
    }

    private static Stream<Arguments> provideVideoElement() {
        return Stream.of(
                Arguments.of(0, 1, 0, 10, true, true),
                Arguments.of(0, 1, 0, 10, false, true),
                Arguments.of(0, 1, 0, 10, true, false),
                Arguments.of(0, 1, 0, 10, false, false),
                Arguments.of(0, 1, 5, 10, true, true),
                Arguments.of(0, 1, 5, 10, false, true),
                Arguments.of(0, 1, 5, 10, true, false),
                Arguments.of(0, 1, 5, 10, false, false),
                Arguments.of(10, 1, 0, 10, true, true),
                Arguments.of(10, 1, 0, 10, false, true),
                Arguments.of(10, 1, 0, 10, true, false),
                Arguments.of(10, 1, 0, 10, false, false),
                Arguments.of(10, 1, 1, 10, true, true),
                Arguments.of(10, 1, 1, 10, false, true),
                Arguments.of(10, 1, 1, 10, true, false),
                Arguments.of(10, 1, 1, 10, false, false),
                Arguments.of(20, 1, 0, 10, true, true),
                Arguments.of(20, 1, 0, 10, false, true),
                Arguments.of(20, 1, 0, 10, true, false),
                Arguments.of(20, 1, 0, 10, false, false)
        );
    }
}
