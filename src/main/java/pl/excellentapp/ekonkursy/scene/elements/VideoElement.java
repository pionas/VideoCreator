package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import pl.excellentapp.ekonkursy.scene.builder.SceneMargin;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
public class VideoElement extends SceneElement {

    private final Path videoFilePath;
    private final boolean loop;
    private final boolean keepLastFrame;
    private int totalFrames;
    private boolean initialized = false;
    private final List<Frame> videoFrames = new ArrayList<>();

    public VideoElement(Path videoFilePath, ElementPosition position, int displayDuration, int delay, int fps, boolean loop, boolean keepLastFrame, Size size, boolean considerMargins) {
        super(position, displayDuration, delay, fps, size, considerMargins);
        this.videoFilePath = videoFilePath;
        this.loop = loop;
        this.keepLastFrame = keepLastFrame;
        loadFrames();
    }

    @Override
    public void render(SceneMargin margin, Mat frame, int currentFrame) {
        if (!initialized || currentFrame < frameStart) {
            return;
        }
        if (currentFrame > frameEnd && !loop && !keepLastFrame) {
            return;
        }
        int frameIndex = currentFrame - frameStart;
        if (frameIndex >= videoFrames.size()) {
            if (loop) {
                frameIndex %= videoFrames.size();
            } else if (!keepLastFrame) {
                return;
            } else {
                frameIndex = videoFrames.size() - 1;
            }
        }

        try {
            Frame videoFrame = videoFrames.get(frameIndex);
            if (videoFrame != null) {
                try (OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat()) {
                    Mat image = converter.convert(videoFrame);
                    addToVideoFrame(margin, frame, image);
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd podczas odtwarzania klatki wideo: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void loadFrames() {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath.toString())) {
            grabber.setFormat("mp4");
            grabber.start();
            Frame frame;
            while ((frame = grabber.grabImage()) != null) {
                videoFrames.add(frame.clone());
            }
            totalFrames = videoFrames.size();
            grabber.stop();
            grabber.release();
            initialized = true;
        } catch (Exception e) {
            System.err.println("Błąd podczas wczytywania klatek: " + e.getMessage());
            initialized = false;
            throw new RuntimeException(e);
        }
    }
}