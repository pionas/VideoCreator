package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import pl.excellentapp.ekonkursy.scene.SceneConfig;

import java.nio.file.Path;

@Getter
public class VideoElement extends SceneElement {

    private final Path videoFilePath;
    private final boolean loop;
    private final boolean keepLastFrame;
    private int totalFrames;
    private boolean initialized = false;
    private FFmpegFrameGrabber grabber;

    public VideoElement(Path videoFilePath, ElementPosition position, int displayDuration, int delay, int fps, boolean loop, boolean keepLastFrame, ElementSize size, boolean considerMargins) {
        super(position, displayDuration, delay, fps, size, considerMargins);
        this.videoFilePath = videoFilePath;
        this.loop = loop;
        this.keepLastFrame = keepLastFrame;
        loadFrames();
    }

    @Override
    public void render(SceneConfig config, Mat frame, int currentFrame) {
        if (!initialized || currentFrame < frameStart) {
            return;
        }
        if (currentFrame > frameEnd && !loop && !keepLastFrame) {
            return;
        }
        int frameIndex = currentFrame - frameStart;
        if (frameIndex >= totalFrames) {
            if (loop) {
                frameIndex %= totalFrames;
            } else if (!keepLastFrame) {
                return;
            } else {
                frameIndex = totalFrames - 1;
            }
        }

        try {
            grabber.setTimestamp((long) (frameIndex * (1000000 / grabber.getFrameRate())));
            Frame videoFrame = grabber.grabImage();
            if (videoFrame != null) {
                try (OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat()) {
                    Mat image = converter.convert(videoFrame);
                    addToVideoFrame(config, frame, image, currentFrame);
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas odtwarzania klatki wideo: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void loadFrames() {
        try {
            grabber = new FFmpegFrameGrabber(videoFilePath.toString());
            grabber.setFormat("mp4");
            grabber.start();
            totalFrames = grabber.getLengthInFrames();
            initialized = true;
        } catch (Exception e) {
            System.err.println("Błąd podczas wczytywania klatek: " + e.getMessage());
            initialized = false;
            throw new RuntimeException(e);
        }
    }
}