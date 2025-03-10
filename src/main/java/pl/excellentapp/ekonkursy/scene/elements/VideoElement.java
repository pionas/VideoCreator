package pl.excellentapp.ekonkursy.scene.elements;

import lombok.Getter;
import org.bytedeco.ffmpeg.global.avcodec;
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
    private int totalFrames = 0;
    private boolean initialized = false;

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

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath.toString())) {
            grabber.setFormat("mp4");
            grabber.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            grabber.start();
            grabber.setFrameNumber(frameIndex);
            Frame videoFrame = grabber.grabImage();
            if (videoFrame != null) {
                try (OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat()) {
                    Mat image = converter.convert(videoFrame);
                    addToVideoFrame(config, frame, image, currentFrame);
                }
            }
            grabber.stop();
        } catch (Exception e) {
            System.err.println("Błąd odtwarzania wideo: " + e.getMessage());
        }
    }

    private void loadFrames() {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath.toString())) {
            grabber.setFormat("mp4");
            grabber.start();
            while (grabber.grabImage() != null) {
                totalFrames++;
            }
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