package pl.excellentapp.ekonkursy.scene.elements;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageElementTest {

    private final ElementPosition mockPosition = mock(ElementPosition.class);
    private final Mat mockFrame = mock(Mat.class);
    private final Path mockFile = mock(Path.class);

    @BeforeEach
    void setUp() {
        when(mockPosition.getLeft()).thenReturn(10);
        when(mockPosition.getTop()).thenReturn(20);
        when(mockFile.toString()).thenReturn("test-image.jpg");
    }

    @ParameterizedTest
    @MethodSource("provideImageElement")
    void testRenderWithinTimeFrame(int currentFrame, int displayDuration, int delay, int fps, boolean keepAfterEnd, int fetchTimes) {
        // given
        ImageElement imageElement = new ImageElement(mockFile, mockPosition, displayDuration, delay, fps, keepAfterEnd, new Size(100, 100), false);
        SceneMargin sceneMargin = SceneMargin.builder().build();

        // when
        imageElement.render(sceneMargin, mockFrame, currentFrame);

        // then
        verify(mockFile, times(fetchTimes)).toString();
    }

    private static Stream<Arguments> provideImageElement() {
        return Stream.of(
                Arguments.of(0, 1, 0, 10, true, 1),
                Arguments.of(0, 1, 0, 10, false, 1),
                Arguments.of(0, 1, 5, 10, true, 0),
                Arguments.of(0, 1, 5, 10, false, 0),
                Arguments.of(10, 1, 0, 10, true, 1),
                Arguments.of(10, 1, 0, 10, false, 1),
                Arguments.of(10, 1, 1, 10, true, 1),
                Arguments.of(10, 1, 1, 10, false, 1),
                Arguments.of(20, 1, 0, 10, true, 1),
                Arguments.of(20, 1, 0, 10, false, 0)
        );
    }
}