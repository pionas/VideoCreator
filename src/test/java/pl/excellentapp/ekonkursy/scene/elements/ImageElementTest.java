package pl.excellentapp.ekonkursy.scene.elements;

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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageElementTest {

    private final ElementPosition mockPosition = mock(ElementPosition.class);
    private final Path file = new File("./images/e-KonkursyInfo.jpg").toPath();

    @BeforeEach
    void setUp() {
        when(mockPosition.getLeft()).thenReturn(10);
        when(mockPosition.getTop()).thenReturn(20);
    }

    @ParameterizedTest
    @MethodSource("provideImageElement")
    void testRenderWithinTimeFrame(int currentFrame, int displayDuration, int delay, int fps, boolean keepAfterEnd, int fetchTimes) {
        // given
        ImageElement imageElement = spy(new ImageElement(file, mockPosition, displayDuration, delay, fps, keepAfterEnd, new ElementSize(100, 100), false));
        SceneConfig sceneConfig = new SceneConfig(100, 100, Color.WHITE, Color.BLACK, 1, SceneMargin.builder().top(0).right(0).bottom(0).left(0).build(), List.of());
        Mat mockFrame = new Mat(100, 100, 16);

        // when
        imageElement.render(sceneConfig, mockFrame, currentFrame);

        // then
        verify(imageElement, times(fetchTimes)).addToVideoFrame(any(SceneConfig.class), any(Mat.class), any(Mat.class), anyInt());
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